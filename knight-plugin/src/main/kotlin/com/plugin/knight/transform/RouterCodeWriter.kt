package com.plugin.knight.transform

import org.objectweb.asm.*

class RouterCodeWriter(private val resultMap: HashMap<String, RouterTransform.NameIntercept>) {
    fun getCodeByte(): ByteArray {
        val cw = ClassWriter(0)
        var fv: FieldVisitor
        var mv: MethodVisitor
        var av0: AnnotationVisitor
        //创建类
        cw.visit(
            Opcodes.V1_7,
            Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
            "com/lzx/knight/router/RouterTable",
            null,
            "java/lang/Object",
            arrayOf("com/lzx/knight/router/IRouterTable")
        )
        cw.visitSource("RouterTable.java", null)
        //创建变量
        run {
            fv = cw.visitField(
                Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC,
                "routerMap",
                "Ljava/util/HashMap;",
                "Ljava/util/HashMap<Ljava/lang/String;Landroid/util/Pair<Ljava/lang/String;Ljava/lang/String;>;>;",
                null
            )
            fv.visitEnd()
        }
        //构造函数
        run {
            mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null)
            mv.visitCode()
            val l0 = Label()
            mv.visitLabel(l0)
            mv.visitLineNumber(7, l0)
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
            mv.visitInsn(Opcodes.RETURN)
            val l1: Label = Label()
            mv.visitLabel(l1)
            mv.visitLocalVariable(
                "this",
                "Lcom/lzx/knight/router/RouterTable;",
                null,
                l0,
                l1,
                0
            )
            mv.visitMaxs(1, 1)
            mv.visitEnd()
        }
        //addRouter 方法
        run {
            mv = cw.visitMethod(
                Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC, "addRouter",
                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", null, null
            )
            mv.visitCode()
            val l0 = Label()
            mv.visitLabel(l0)
            mv.visitLineNumber(15, l0)
            mv.visitFieldInsn(
                Opcodes.GETSTATIC, "com/lzx/knight/router/RouterTable", "routerMap",
                "Ljava/util/HashMap;"
            )
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "containsKey", "(Ljava/lang/Object;)Z",
                false
            )
            val l1 = Label()
            mv.visitJumpInsn(Opcodes.IFNE, l1)
            val l2 = Label()
            mv.visitLabel(l2)
            mv.visitLineNumber(16, l2)
            mv.visitFieldInsn(
                Opcodes.GETSTATIC, "com/lzx/knight/router/RouterTable", "routerMap",
                "Ljava/util/HashMap;"
            )
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitTypeInsn(Opcodes.NEW, "android/util/Pair")
            mv.visitInsn(Opcodes.DUP)
            mv.visitVarInsn(Opcodes.ALOAD, 1)
            mv.visitVarInsn(Opcodes.ALOAD, 2)
            mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL, "android/util/Pair", "<init>",
                "(Ljava/lang/Object;Ljava/lang/Object;)V", false
            )
            mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "put",
                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false
            )
            mv.visitInsn(Opcodes.POP)
            mv.visitLabel(l1)
            mv.visitLineNumber(18, l1)
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null)
            mv.visitInsn(Opcodes.RETURN)
            val l3 = Label()
            mv.visitLabel(l3)
            mv.visitLocalVariable("path", "Ljava/lang/String;", null, l0, l3, 0)
            mv.visitLocalVariable("className", "Ljava/lang/String;", null, l0, l3, 1)
            mv.visitLocalVariable("intercept", "Ljava/lang/String;", null, l0, l3, 2)
            mv.visitMaxs(6, 3)
            mv.visitEnd()
//            mv = cw.visitMethod(
//                Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC, "addRouter",
//                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", null, null
//            )
//            mv.visitCode()
//            val l0 = Label()
//            mv.visitLabel(l0)
//            mv.visitLineNumber(15, l0)
//            mv.visitFieldInsn(
//                Opcodes.GETSTATIC, "com/lzx/knight/router/RouterTable", "routerMap",
//                "Ljava/util/HashMap;"
//            )
//            mv.visitVarInsn(Opcodes.ALOAD, 0)
//            mv.visitTypeInsn(Opcodes.NEW, "android/util/Pair")
//            mv.visitInsn(Opcodes.DUP)
//            mv.visitVarInsn(Opcodes.ALOAD, 1)
//            mv.visitVarInsn(Opcodes.ALOAD, 2)
//            mv.visitMethodInsn(
//                Opcodes.INVOKESPECIAL, "android/util/Pair", "<init>",
//                "(Ljava/lang/Object;Ljava/lang/Object;)V", false
//            )
//            mv.visitMethodInsn(
//                Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "put",
//                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false
//            )
//            mv.visitInsn(Opcodes.POP)
//            val l1 = Label()
//            mv.visitLabel(l1)
//            mv.visitLineNumber(16, l1)
//            mv.visitInsn(Opcodes.RETURN)
//            val l2 = Label()
//            mv.visitLabel(l2)
//            mv.visitLocalVariable("path", "Ljava/lang/String;", null, l0, l2, 0)
//            mv.visitLocalVariable("className", "Ljava/lang/String;", null, l0, l2, 1)
//            mv.visitLocalVariable("intercept", "Ljava/lang/String;", null, l0, l2, 2)
//            mv.visitMaxs(6, 3)
//            mv.visitEnd()
        }
        //getRouterMap 方法
        run {
            mv = cw.visitMethod(
                Opcodes.ACC_PUBLIC, "getRouterMap", "()Ljava/util/HashMap;",
                "()Ljava/util/HashMap<Ljava/lang/String;Landroid/util/Pair<Ljava/lang/String;Ljava/lang/String;>;>;",
                null
            )
            //添加 Override 注解
            av0 = mv.visitAnnotation("Ljava.lang/Override;", false)
            av0.visitEnd()

            mv.visitCode()
            val l0 = Label()
            mv.visitLabel(l0)
            mv.visitLineNumber(15, l0)
            mv.visitFieldInsn(
                Opcodes.GETSTATIC, "com/lzx/knight/router/RouterTable", "routerMap",
                "Ljava/util/HashMap;"
            )
            mv.visitInsn(Opcodes.ARETURN)
            val l1: Label = Label()
            mv.visitLabel(l1)
            mv.visitLocalVariable(
                "this",
                "Lcom/lzx/knight/router/RouterTable;",
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
            mv = cw.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null)
            mv.visitCode()
            val l0 = Label()
            mv.visitLabel(l0)
            mv.visitLineNumber(8, l0)
            mv.visitTypeInsn(Opcodes.NEW, "java/util/HashMap")
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false)
            mv.visitFieldInsn(
                Opcodes.PUTSTATIC, "com/lzx/knight/router/RouterTable", "routerMap",
                "Ljava/util/HashMap;"
            )
            var i = 10
            resultMap.forEach {
                val l1 = Label()
                mv.visitLabel(l1)
                mv.visitLineNumber(i++, l1)
                mv.visitLdcInsn(it.key)
                mv.visitLdcInsn(it.value.className)
                mv.visitLdcInsn(it.value.intercept)
                mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC, "com/lzx/knight/router/RouterTable", "addRouter",
                    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false
                )
            }

            val l2 = Label()
            mv.visitLabel(l2)
            mv.visitLineNumber(12, l2)
            mv.visitInsn(Opcodes.RETURN)
            mv.visitMaxs(3, 0)
            mv.visitEnd()
        }
        cw.visitEnd()

        return cw.toByteArray()
    }
}