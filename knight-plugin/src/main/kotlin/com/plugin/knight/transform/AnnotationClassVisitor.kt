package com.plugin.knight.transform

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import java.util.*


class AnnotationClassVisitor(
    api: Int, cv: ClassVisitor?,
    private val serviceMap: HashMap<String, String>,
    private val serviceImplMap: HashMap<String, String>
) : ClassVisitor(api, cv) {

    companion object {
        private const val SERVICE_ANN = "Lcom/lzx/knight/annotations/KnightService;"
        private const val SERVICE_IMPL = "Lcom/lzx/knight/annotations/KnightImpl;"
    }

    private var className: String? = null
    private var interfaces: Array<out String>? = null
    private var superName: String? = null
    private var isAbstractClass: Boolean = false

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
        this.superName = superName

        val isInterface = (access and Opcodes.ACC_INTERFACE) != 0
        val isAbs = (access and Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT

        isAbstractClass = !isInterface && isAbs
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        val annotationVisitor = super.visitAnnotation(descriptor, visible)
        return when (descriptor) {
            SERVICE_ANN -> {
                //如果是抽象类，并且有注解，并且实现了某个接口，记下来
                if (isAbstractClass && !interfaces.isNullOrEmpty()) {
                    className?.let { serviceMap.put(it, it) }
                }
                annotationVisitor
            }
            SERVICE_IMPL -> {
                ImplVisitor(
                    api,
                    annotationVisitor,
                    className,
                    superName,
                    interfaces,
                    serviceMap,
                    serviceImplMap
                )
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
    private val superName: String?,
    private var interfaces: Array<out String>?,
    private var serviceMap: HashMap<String, String>,
    private val serviceImplMap: HashMap<String, String>
) : AnnotationVisitor(api, av) {

    var isVisit: Boolean = false

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        className?.let { className ->
            //接口
            interfaces?.forEach {
                val key = it + "_" + (value as String)
                serviceImplMap[key] = className
            }
            //抽象类
            if (serviceMap.containsKey(superName)) {
                superName?.let {
                    val key = it + "_" + (value as String)
                    serviceImplMap[key] = className
                }
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
            if (serviceMap.containsKey(superName)) {
                superName?.let {
                    val key = it + "_KnightDefault"
                    serviceImplMap[key] = className
                }
            }
        }
    }
}