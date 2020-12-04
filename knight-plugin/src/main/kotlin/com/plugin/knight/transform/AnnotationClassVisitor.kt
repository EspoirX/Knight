package com.plugin.knight.transform

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import java.util.*

class AnnotationClassVisitor(
    api: Int, cv: ClassVisitor?,
    private val serviceImplMap: HashMap<String, String>
) : ClassVisitor(api, cv) {

    companion object {
        private const val SERVICE_ANN = "Lcom/lzx/knight/annotations/KnightService;"
        private const val SERVICE_IMPL = "Lcom/lzx/knight/annotations/KnightImpl;"
    }

    private var className: String? = null
    private var interfaces: Array<out String>? = null

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name
        this.interfaces = interfaces
        println("扫描的类 = " + className)
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        val annotationVisitor = super.visitAnnotation(descriptor, visible)
        return when (descriptor) {
            SERVICE_ANN -> {
                annotationVisitor
            }
            SERVICE_IMPL -> {
                ImplVisitor(api, annotationVisitor, className, interfaces, serviceImplMap)
            }
            else -> {
                annotationVisitor
            }
        }
    }

}

class ImplVisitor internal constructor(
    api: Int, av: AnnotationVisitor,
    private val className: String?,
    private var interfaces: Array<out String>?,
    private val serviceImplMap: HashMap<String, String>
) : AnnotationVisitor(api, av) {

    var isVisit: Boolean = false

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        className?.let { className ->
            interfaces?.forEach {
                val key = it + "_" + (value as String)
                serviceImplMap[key] = className
            }
            isVisit = true
        }
    }

    override fun visitEnd() {
        super.visitEnd()
        if (!isVisit && className != null) {
            interfaces?.forEach {
                val key = it + "_KnightDefault"
                serviceImplMap[key] = className
            }
        }
    }
}