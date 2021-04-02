package com.plugin.knight

import com.android.build.api.transform.Format
import com.android.build.api.transform.Status
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.utils.FileUtils
import java.io.File
import java.io.FileOutputStream

object KnightConfig {

    var isDebug = true

    fun showLog(msg: String) {
        if (isDebug) {
            println(msg)
        }
    }

    fun shouldProcessClass(name: String?): Boolean {
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

    fun handlerChangeOrRemove(
        isIncremental: Boolean,
        inputs: MutableCollection<TransformInput>?,
        outputProvider: TransformOutputProvider?,
        listener: OnModifyListener?
    ) {
        if (!isIncremental) return
        inputs?.forEach { it ->
            it.directoryInputs.forEach { directoryInput ->
                val dest = outputProvider?.getContentLocation(
                    directoryInput.name, directoryInput.contentTypes,
                    directoryInput.scopes, Format.DIRECTORY
                )
                val map = directoryInput.changedFiles
                val dir = directoryInput.file
                map.forEach {
                    val status = it.value
                    val file: File = it.key
                    val destFilePath =
                        file.absolutePath.replace(dir.absolutePath, dest!!.absolutePath)
                    val destFile = File(destFilePath)
                    when (status) {
                        Status.ADDED, Status.CHANGED -> {
                            val absolutePath =
                                file.absolutePath.replace(dir.absolutePath + File.separator, "")
                            val className: String = absolutePath.path2Classname()
                            if (absolutePath.endsWith(".class")) {
                                listener?.onClassChange(className)
                            }
                        }
                        Status.REMOVED -> {
                            if (destFile.isDirectory) {
                                for (classFile in FileUtils.getAllFiles(destFile)) {
                                    deleteSingle(classFile, dest, listener)
                                }
                            } else {
                                deleteSingle(destFile, dest, listener)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun deleteSingle(classFile: File, dest: File, listener: OnModifyListener?) {
        if (classFile.name.endsWith(".class")) {
            val absolutePath = classFile.absolutePath.replace(
                dest.absolutePath +
                        File.separator, ""
            )
            val className: String = absolutePath.path2Classname()
            listener?.onClassDelete(className)
        }
    }

    private fun String.path2Classname(): String {
        return this.replace(File.separator, ".").replace(".class", "")
    }

    fun saveFile(mTempDir: File?, modifiedClassBytes: ByteArray?): File? {
        var modified: File? = null
        try {
            if (modifiedClassBytes != null) {
                modified = mTempDir
                if (modified?.exists() == true) {
                    modified.delete()
                }
                modified?.createNewFile()
                modified?.let { FileOutputStream(it).use { it.write(modifiedClassBytes) } }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return modified
    }

    interface OnModifyListener {
        fun onClassChange(className: String)
        fun onClassDelete(className: String)
    }
}