package com.plugin.knight.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.ImmutableSet
import com.plugin.knight.KnightConfig
import com.quinn.hunter.transform.HunterTransform
import com.quinn.hunter.transform.asm.BaseWeaver
import org.apache.commons.compress.utils.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class RouterTransform(project: Project?) : HunterTransform(project) {

    data class AnnInfo(
        var className: String?,
        var scheme: String?,
        var arrayName: String?,
        var annValue: String?
    )

    data class NameIntercept(var className: String?, var intercept: String?)

    private val routerList = mutableListOf<AnnInfo>()

    init {
        this.bytecodeWeaver = object : BaseWeaver() {
            override fun isWeavableClass(fullQualifiedClassName: String?): Boolean {
                return KnightConfig.shouldProcessClass(fullQualifiedClassName)
            }

            override fun wrapClassWriter(classWriter: ClassWriter?): ClassVisitor {
                return RouterClassVisitor(
                    Opcodes.ASM7,
                    classWriter,
                    routerList
                )
            }
        }
    }

    override fun transform(
        context: Context?,
        inputs: MutableCollection<TransformInput>?,
        referencedInputs: MutableCollection<TransformInput>?,
        outputProvider: TransformOutputProvider?,
        isIncremental: Boolean
    ) {
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
        val initClasses = Collections.newSetFromMap(ConcurrentHashMap<String, Boolean>())
        val deleteClasses = Collections.newSetFromMap(ConcurrentHashMap<String, Boolean>())

        KnightConfig.handlerChangeOrRemove(
            isIncremental,
            inputs,
            outputProvider,
            object : KnightConfig.OnModifyListener {
                override fun onClassChange(className: String) {
                    KnightConfig.showLog("ChangeCallback -> className = " + className)
                    initClasses.add(className)
                }

                override fun onClassDelete(className: String) {
                    KnightConfig.showLog("DeleteCallback -> className = " + className)
                    deleteClasses.add(className)
                }
            })

        val dest = outputProvider?.getContentLocation(
            "Knight",
            TransformManager.CONTENT_CLASS,
            ImmutableSet.of(QualifiedContent.Scope.PROJECT),
            Format.DIRECTORY
        ) ?: return

        val file = File(dest, "com/lzx/knight/router/RouterTable.class")
        val resultMap = generateResultMap()
        if (!file.exists()) {
            generateRouterClass(file, resultMap)
        } else {
            val inputStream = FileInputStream(file)
            val sourceClassBytes = IOUtils.toByteArray(inputStream)
            val modifiedClassBytes =
                modifyClass(sourceClassBytes, initClasses, deleteClasses, resultMap)
            if (modifiedClassBytes != null) {
                KnightConfig.saveFile(file, modifiedClassBytes)
            }
        }
    }


    private fun generateRouterClass(file: File, resultMap: HashMap<String, NameIntercept>) {
        val routerCodeWriter = RouterCodeWriter(resultMap)
        file.parentFile.mkdirs()
        FileOutputStream(file).use { it.write(routerCodeWriter.getCodeByte()) }
        KnightConfig.showLog("生成的路由文件地址 = " + file.absolutePath)
    }

    private fun generateResultMap(): HashMap<String, NameIntercept> {
        val resultMap = hashMapOf<String, NameIntercept>()
        routerList
            .filter { !it.className.isNullOrEmpty() && !it.annValue.isNullOrEmpty() && it.arrayName == "path" }
            .forEach {
                val finalScheme = if (it.scheme == "null" || it.scheme.isNullOrEmpty()) {
                    "KnightRouter://"
                } else {
                    it.scheme + "://"
                }
                val key = finalScheme + it.annValue
                resultMap[key] = NameIntercept(it.className!!, "")
            }

        KnightConfig.showLog("====================================")
        routerList
            .filter { !it.className.isNullOrEmpty() && !it.annValue.isNullOrEmpty() && it.arrayName == "interceptors" }
            .forEach { info ->
                resultMap.filter { it.value.className == info.className }.forEach {
                    it.value.intercept += info.annValue
                }
            }
        resultMap.forEach {
            KnightConfig.showLog("Router映射表 key = " + it.key + " className = " + it.value.className + " intercept = " + it.value.intercept)
        }
        return resultMap
    }

    @Throws(IOException::class)
    fun modifyClass(
        srcClass: ByteArray?,
        items: MutableSet<String>,
        deleteItems: MutableSet<String>,
        resultMap: HashMap<String, NameIntercept>
    ): ByteArray? {
        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
        val methodFilterCV = ClassFilterVisitor(
            Opcodes.ASM7,
            classWriter,
            items,
            deleteItems,
            resultMap
        )
        val cr = ClassReader(srcClass)
        cr.accept(methodFilterCV, ClassReader.SKIP_DEBUG)
        return classWriter.toByteArray()
    }
}