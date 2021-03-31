package com.plugin.knight.transform

import com.plugin.knight.KnightConfig
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

class RouterClassVisitor(
    api: Int,
    cv: ClassVisitor?,
    private val routerList: MutableList<RouterTransform.AnnInfo>
) : ClassVisitor(api, cv) {

    companion object {
        private const val KnightRouter = "Lcom/lzx/knight/annotations/KnightRouter;"
    }

    private var className: String? = null
    private var interfaces: Array<out String>? = null
    private var superName: String? = null
    private var isAbstractClass: Boolean = false
    private var isInterfaceClass: Boolean = false

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        this.className = name
        this.interfaces = interfaces
        this.superName = superName
        isInterfaceClass = (access and Opcodes.ACC_INTERFACE) != 0
        val isAbs = (access and Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT
        isAbstractClass = !isInterfaceClass && isAbs
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        val av = super.visitAnnotation(descriptor, visible)
        if (descriptor == KnightRouter) {
            if (isInterfaceClass) {
                throw RuntimeException("KnightRouter 注解不能用在接口上")
            }
            return RouterAnnVisitor(api, av, className, routerList)
        }
        return av
    }


}

class RouterAnnVisitor constructor(
    api: Int,
    av: AnnotationVisitor,
    private val className: String?,
    private val routerList: MutableList<RouterTransform.AnnInfo>
) : AnnotationVisitor(api, av) {

    private var scheme: String? = null

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        if (name == "scheme") {
            val valueStr = value as String?
            scheme = if (valueStr.isNullOrEmpty()) "KnightRouter://" else valueStr
        }
    }

    override fun visitArray(name: String?): AnnotationVisitor {
        return if (name == "path" || name == "interceptors") {
            AnnArrayVisitor(api, super.visitArray(name), className, name, scheme, routerList)
        } else {
            super.visitArray(name)
        }
    }
}

class AnnArrayVisitor constructor(
    api: Int,
    av: AnnotationVisitor,
    private var className: String?,
    private val arrayName: String?,
    private var scheme: String?,
    private val routerList: MutableList<RouterTransform.AnnInfo>
) : AnnotationVisitor(api, av) {

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        KnightConfig.showLog("Router 扫描-> className = $className ，scheme = $scheme ，arrayName = $arrayName ，value = $value")
        routerList.add(RouterTransform.AnnInfo(className, scheme, arrayName, value.toString()))
    }
}