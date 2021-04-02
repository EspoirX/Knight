package com.plugin.knight.transform

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.util.*

class ClassFilterVisitor(
    api: Int,
    cv: ClassVisitor,
    private val classItems: MutableSet<String>,
    private val deleteItems: MutableSet<String>,
    private val resultMap: HashMap<String, RouterTransform.NameIntercept>
) : ClassVisitor(api, cv) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == "<clinit>" && descriptor == "()V") {
            val static = (access and Opcodes.ACC_STATIC) > 0
            return RouterMethodVisitor(
                Opcodes.ASM7,
                mv,
                static,
                resultMap
            )
        }
        return mv
    }
}

class RouterMethodVisitor(
    api: Int,
    mv: MethodVisitor,
    private val static: Boolean,
    private val resultMap: HashMap<String, RouterTransform.NameIntercept>
) : MethodVisitor(api, mv) {


    override fun visitInsn(opcode: Int) {
        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
            resultMap.forEach {
                if (!static) {
                    mv.visitVarInsn(Opcodes.ALOAD, 0)
                }
                if (static) {
                    mv.visitLdcInsn(it.key)
                    mv.visitLdcInsn(it.value.className)
                    mv.visitLdcInsn(it.value.intercept)
                    mv.visitMethodInsn(
                        Opcodes.INVOKESTATIC, "com/lzx/knight/router/RouterTable", "addRouter",
                        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false
                    )
                } else {
                    mv.visitLdcInsn(it.key)
                    mv.visitLdcInsn(it.value.className)
                    mv.visitLdcInsn(it.value.intercept)
                    mv.visitMethodInsn(
                        Opcodes.INVOKESPECIAL, "com/lzx/knight/router/RouterTable", "addRouter",
                        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false
                    )
                }
            }
        }
        super.visitInsn(opcode)
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack + 4, maxLocals)
    }
}