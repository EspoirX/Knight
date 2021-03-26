package com.plugin.knight.transform

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

class KnightClassVisitor(
    api: Int, cv: ClassVisitor?,
    private val knightServiceList: MutableList<String>,
    private val knightImplList: MutableList<KnightTransform.KnightImplInfo>
) : ClassVisitor(api, cv) {

    companion object {
        private const val KnightService = "Lcom/lzx/knight/annotations/KnightService;"
        private const val KnightImpl = "Lcom/lzx/knight/annotations/KnightImpl;"
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
        when (descriptor) {
            KnightService -> {
                if (isAbstractClass || isInterfaceClass) {
                    className?.let {
                        if (!knightServiceList.contains(it)) {
                            knightServiceList.add(it)
                        }
                    }
                }
                return av
            }
            KnightImpl -> {
                return KnightImplVisitor(
                    api,
                    av,
                    className,
                    superName,
                    isAbstractClass,
                    isInterfaceClass,
                    interfaces,
                    knightServiceList,
                    knightImplList
                )
            }
            else -> return av
        }
    }
}

class KnightImplVisitor constructor(
    api: Int, av: AnnotationVisitor,
    private val className: String?,
    private val superName: String?,
    private val isAbstractClass: Boolean,
    private val isInterfaceClass: Boolean,
    private val interfaces: Array<out String>?,
    private val knightServiceList: MutableList<String>,
    private val knightImplList: MutableList<KnightTransform.KnightImplInfo>
) : AnnotationVisitor(api, av) {

    var annValue: String? = null

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
        annValue = value as String
    }

    override fun visitEnd() {
        super.visitEnd()
        className?.let {
            knightImplList.add(
                KnightTransform.KnightImplInfo(
                    it,
                    interfaces?.toMutableList(),
                    superName,
                    annValue
                )
            )
        }
    }
}