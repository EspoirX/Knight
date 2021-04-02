package com.lzx.compiler;

import com.google.auto.service.AutoService;
import com.lzx.annotation.KnightRouter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
public class RouterProcessor extends BaseProcessor {

    public static final String PKG_NAME = "com.lzx.knight.router";

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedTypes = new HashSet<>();
        supportedTypes.add(KnightRouter.class.getCanonicalName());
        return supportedTypes;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        Set<? extends Element> routerElementSet = roundEnv.getElementsAnnotatedWith(KnightRouter.class);
        if (routerElementSet.size() == 0) {
            return false;
        }
        CodeBlock.Builder builder = CodeBlock.builder();
        CodeBlock code = null;
        for (Element element : routerElementSet) {
            TypeElement type = (TypeElement) element;
            if (element.getKind().isClass() && validateClass(type)) { // 注解在Class上的Route
                Symbol.ClassSymbol cls = (Symbol.ClassSymbol) element;
                KnightRouter knightRouter = cls.getAnnotation(KnightRouter.class);
                String className = ((Symbol.ClassSymbol) element).getQualifiedName().toString();
                String scheme = knightRouter.scheme();
                String[] paths = knightRouter.path();
                CodeBlock interceptors = buildInterceptors(getInterceptors(knightRouter));
                for (String path : paths) {
                    String realScheme = (scheme == null || scheme.equals("")) ? "KnightRouter://" : scheme + "://";
                    String key = realScheme + path;
                    builder.addStatement("registerRouter.register($S,$S$L)", //这里最后两个故意不加都好，在 buildInterceptors 里面加，因为如果加了的话，最后一个参数没有的时候会多出一个逗号
                            key,
                            className,
                            interceptors);
                }
                code = builder.build(); //方法体内的代码
            }
        }
        //生成类
        if (code != null) {
            MethodSpec.Builder methodSpec = MethodSpec.methodBuilder("registerRouter")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(code)
                    .addParameter(ClassName.get(PKG_NAME, "RouterRegister"), "registerRouter");
            TypeSpec typeSpec = TypeSpec.classBuilder("RouterTable")
                    .addSuperinterface(ClassName.get(PKG_NAME, "IRouterTable"))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodSpec.build())
                    .build();
            try {
                JavaFile javaFile = JavaFile.builder(PKG_NAME, typeSpec).build();
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 创建Interceptors
     */
    public CodeBlock buildInterceptors(List<? extends TypeMirror> interceptors) {
        CodeBlock.Builder b = CodeBlock.builder();
        if (interceptors != null && interceptors.size() > 0) {
            for (TypeMirror type : interceptors) {
                if (type instanceof Type.ClassType) {
                    Symbol.TypeSymbol e = ((Type.ClassType) type).asElement();
                    if (e instanceof Symbol.ClassSymbol && isInterceptor(e)) {
                        b.add(" , new $T()", e);
                    }
                }
            }
        }
        return b.build();
    }

    private static List<? extends TypeMirror> getInterceptors(KnightRouter knightRouter) {
        try {
            knightRouter.interceptors();
        } catch (MirroredTypesException mte) {
            return mte.getTypeMirrors();
        }
        return null;
    }
}
