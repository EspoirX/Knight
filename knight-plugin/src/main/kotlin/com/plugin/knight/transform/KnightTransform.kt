package com.plugin.knight.transform

import com.android.build.api.transform.Context
import com.android.build.api.transform.Format
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
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

    init {
        this.bytecodeWeaver = object : BaseWeaver() {
            override fun isWeavableClass(fullQualifiedClassName: String?): Boolean {
                return shouldProcessClass(fullQualifiedClassName)
            }

            override fun wrapClassWriter(classWriter: ClassWriter?): ClassVisitor {
                return AnnotationClassVisitor(Opcodes.ASM7, classWriter, serviceImplMap)
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

        val f = FileOutputStream("com.lzx.knight.KnightServiceManager.class")
        f.write(byteCodeWriter.dump())
        f.close()
    }

    private fun shouldProcessClass(name: String?): Boolean {
        return !(name.isNullOrEmpty() ||
                name.contains("META-INF") ||
                name.contains("R\$") ||
                name.endsWith("R.class") ||
                name.endsWith("BuildConfig.class") ||
                name.endsWith("Spear.class") ||
                name.endsWith("SpearImpl.class") ||
                name.endsWith("SpearService.class") ||
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
