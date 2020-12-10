package com.plugin.knight.transform

import jdk.internal.org.objectweb.asm.Opcodes.*
import org.objectweb.asm.*
import java.util.*

/**
 * 创建 KnightServiceManager 类
 */
class KnightByteCodeWriter(
    private val serviceImplMap: HashMap<String, String>
) {

    fun dump(): ByteArray {
        val cw = ClassWriter(0)
        var fv: FieldVisitor
        var mv: MethodVisitor
        var av0: AnnotationVisitor
        //创建类
        cw.visit(
            V1_7, ACC_PUBLIC + ACC_SUPER, "com/lzx/knight/KnightServiceManager", null,
            "java/lang/Object", arrayOf("com/lzx/knight/IServiceManager")
        )
        cw.visitSource("KnightServiceManager.java", null)
        //创建变量
        run {
            fv = cw.visitField(
                ACC_PRIVATE + ACC_FINAL + ACC_STATIC, "serviceImplMap", "Ljava/util/HashMap;",
                "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/String;>;", null
            )
            fv.visitEnd()
        }
        //构造函数
        run {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
            mv.visitCode()
            val l0 = Label()
            mv.visitLabel(l0)
            mv.visitLineNumber(7, l0)
            mv.visitVarInsn(ALOAD, 0)
            mv.visitMethodInsn(
                INVOKESPECIAL,
                "java/lang/Object",
                "<init>",
                "()V",
                false
            )
            mv.visitInsn(RETURN)
            val l1 = Label()
            mv.visitLabel(l1)
            mv.visitLocalVariable(
                "this",
                "Lcom/lzx/knight/KnightServiceManager;",
                null,
                l0,
                l1,
                0
            )
            mv.visitMaxs(1, 1)
            mv.visitEnd()
        }
        // getServiceImplMap 方法
        run {
            mv = cw.visitMethod(
                ACC_PUBLIC, "getServiceImplMap", "()Ljava/util/HashMap;",
                "()Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/String;>;", null
            )
            //添加 Override 注解
            av0 = mv.visitAnnotation("Ljava.lang/Override;", false)
            av0.visitEnd()

            mv.visitCode()
            val l0 = Label()
            mv.visitLabel(l0)
            mv.visitLineNumber(17, l0)
            mv.visitFieldInsn(
                GETSTATIC,
                "com/lzx/knight/KnightServiceManager",
                "serviceImplMap",
                "Ljava/util/HashMap;"
            )
            mv.visitInsn(ARETURN)
            val l1 = Label()
            mv.visitLabel(l1)
            mv.visitLocalVariable(
                "this",
                "Lcom/lzx/knight/KnightServiceManager;",
                null,
                l0,
                l1,
                0
            )
            mv.visitMaxs(1, 1)
            mv.visitEnd()
        }
        //static 模块
        run {
            mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null)
            mv.visitCode()
            val l1 = Label()
            mv.visitLabel(l1)
            mv.visitLineNumber(10, l1)
            mv.visitTypeInsn(NEW, "java/util/HashMap")
            mv.visitInsn(DUP)
            mv.visitMethodInsn(
                INVOKESPECIAL,
                "java/util/HashMap",
                "<init>",
                "()V",
                false
            )
            mv.visitFieldInsn(
                PUTSTATIC,
                "com/lzx/knight/KnightServiceManager",
                "serviceImplMap",
                "Ljava/util/HashMap;"
            )
            //循环添加数据
            var i = 20
            serviceImplMap.forEach {
                val l3 = Label()
                mv.visitLabel(l3)
                mv.visitLineNumber(i++, l3)
                mv.visitFieldInsn(
                    GETSTATIC,
                    "com/lzx/knight/KnightServiceManager",
                    "serviceImplMap",
                    "Ljava/util/HashMap;"
                )
                mv.visitLdcInsn(it.key)
                mv.visitLdcInsn(it.value)
                mv.visitMethodInsn(
                    INVOKEVIRTUAL, "java/util/HashMap", "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false
                )
                mv.visitInsn(POP)
            }

            val l7 = Label()
            mv.visitLabel(l7)
            mv.visitLineNumber(27, l7)
            mv.visitInsn(RETURN)
            mv.visitMaxs(3, 0)
            mv.visitEnd()
        }
        cw.visitEnd()
        return cw.toByteArray()
    }




}

