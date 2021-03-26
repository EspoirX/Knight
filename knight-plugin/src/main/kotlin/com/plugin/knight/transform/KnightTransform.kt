package com.plugin.knight.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.ImmutableSet
import com.plugin.knight.KnightConfig
import com.quinn.hunter.transform.HunterTransform
import com.quinn.hunter.transform.asm.BaseWeaver
import org.gradle.api.Project
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileOutputStream

class KnightTransform(project: Project?) : HunterTransform(project) {

    private val knightServiceList = mutableListOf<String>()
    private val knightImplList = mutableListOf<KnightImplInfo>()
    var ms: Long = 0

    init {
        ms = System.currentTimeMillis()
        this.bytecodeWeaver = object : BaseWeaver() {
            override fun isWeavableClass(fullQualifiedClassName: String?): Boolean {
                return shouldProcessClass(fullQualifiedClassName)
            }

            override fun wrapClassWriter(classWriter: ClassWriter?): ClassVisitor {
                return KnightClassVisitor(
                    Opcodes.ASM7,
                    classWriter,
                    knightServiceList,
                    knightImplList
                )
            }
        }
    }

    private fun String.getMapKey(key: String?): String {
        return if (key.isNullOrEmpty()) this else this + "_" + key
    }

    override fun transform(
        context: Context?,
        inputs: MutableCollection<TransformInput>?,
        referencedInputs: MutableCollection<TransformInput>?,
        outputProvider: TransformOutputProvider?,
        isIncremental: Boolean
    ) {
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
        val dest = outputProvider?.getContentLocation(
            "Knight",
            TransformManager.CONTENT_CLASS,
            ImmutableSet.of(QualifiedContent.Scope.PROJECT),
            Format.DIRECTORY
        ) ?: return
        val map = hashMapOf<String, String>()
        knightImplList.forEach { it ->
            val interfaces = it.interfaces
            if (interfaces.isNullOrEmpty()) {
                //抽象类的情况
                if (!it.superClass.isNullOrEmpty() && knightServiceList.contains(it.superClass)) {
                    map[it.superClass!!.getMapKey(it.key)] = it.className
                }
            } else {
                //过滤实现了 KnightService 的接口,如果有多个，取第一个
                val knightInterface =
                    interfaces.filter { knightServiceList.contains(it) }.getOrNull(0)
                if (knightInterface != null) {
                    map[knightInterface] = it.className
                } else {
                    //如果找不到接口，则查看一下继承的类有没有
                    if (!it.superClass.isNullOrEmpty() && knightServiceList.contains(it.superClass)) {
                        map[it.superClass!!.getMapKey(it.key)] = it.className
                    }
                }
            }
        }
        if (KnightConfig.isDebug) {
            KnightConfig.showLog("======================================")
            knightServiceList.forEach {
                KnightConfig.showLog("knightService = $it")
            }
            KnightConfig.showLog("                                        ")
            KnightConfig.showLog("======================================")
            knightImplList.forEach {
                KnightConfig.showLog("knightImpl = " + it.className + " interfaces = " + it.interfaces + " superClass = " + it.superClass + " key = " + it.key)
            }
            KnightConfig.showLog("                                        ")
            KnightConfig.showLog("======================================")
            map.forEach {
                KnightConfig.showLog("key = " + it.key + " value = " + it.value)
            }
        }

        val byteCodeWriter = KnightByteCodeWriter(map)
        val file = File(dest, "com/lzx/knight/KnightServiceManager.class")
        file.parentFile.mkdirs()
        FileOutputStream(file).use { it.write(byteCodeWriter.getCodeByte()) }
        KnightConfig.showLog("Knight AMS 耗时 = " + (System.currentTimeMillis() - ms))
        KnightConfig.showLog("生成的文件地址 = " + file.absolutePath)
    }

    private fun shouldProcessClass(name: String?): Boolean {
        return !(name.isNullOrEmpty() ||
                name.contains("META-INF") ||
                name.contains("R\$") ||
                name.endsWith("R.class") ||
                name.endsWith("BuildConfig.class") ||
                name.endsWith("Knight.class") ||
                name.endsWith("KnightImpl.class") ||
                name.endsWith("KnightService.class") ||
                name.startsWith("kotlinx") ||
                name.startsWith("kotlin") ||
                name.startsWith("com/google/android") ||
                name.startsWith("android/support") ||
                name.startsWith("com.google.android") ||
                name.startsWith("android.support") ||
                name.startsWith("org") ||
                name.startsWith("androidx"))
    }

    data class KnightImplInfo(
        var className: String,
        var interfaces: MutableList<String>?,
        var superClass: String?,
        var key: String?
    )
}
