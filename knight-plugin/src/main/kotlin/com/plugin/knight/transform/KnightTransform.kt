package com.plugin.knight.transform

import com.android.build.api.transform.Context
import com.android.build.api.transform.Format
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.plugin.knight.KnightConfig
import com.plugin.knight.KnightExtension
import com.quinn.hunter.transform.HunterTransform
import com.quinn.hunter.transform.asm.BaseWeaver
import org.gradle.api.Project
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.FileOutputStream
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class KnightTransform(project: Project?) : HunterTransform(project) {

    var serviceImplMap = HashMap<String, String>()
    var serviceMap = HashMap<String, String>()

    init {
        this.bytecodeWeaver = object : BaseWeaver() {
            override fun isWeavableClass(fullQualifiedClassName: String?): Boolean {
                return shouldProcessClass(fullQualifiedClassName)
            }

            override fun wrapClassWriter(classWriter: ClassWriter?): ClassVisitor {
                return AnnotationClassVisitor(Opcodes.ASM7, classWriter, serviceMap, serviceImplMap)
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
        val byteCodeWriter = KnightByteCodeWriter(serviceImplMap)
        val metaFile = outputProvider?.getContentLocation(
            "Knight",
            outputTypes,
            scopes,
            Format.JAR
        ) ?: return

        if (!metaFile.parentFile.exists()) {
            metaFile.parentFile.mkdirs()
        }
        if (metaFile.exists()) {
            metaFile.delete()
        }
        val fos = FileOutputStream(metaFile)
        val jarOutputStream = JarOutputStream(fos)
        val zipEntry = ZipEntry("com/lzx/knight/KnightServiceManager.class")
        jarOutputStream.putNextEntry(zipEntry)
        jarOutputStream.write(byteCodeWriter.dump())
        jarOutputStream.closeEntry()
        jarOutputStream.close()
        fos.close()

//        if (KnightConfig.debugSwitch) {
//            //这个是为了在项目根目录生成一个文件，方便查看debug用，不要也行
//            FileOutputStream("com.lzx.knight.KnightServiceManager.class").use {
//                it.write(byteCodeWriter.dump())
//            }
//        }
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
}
