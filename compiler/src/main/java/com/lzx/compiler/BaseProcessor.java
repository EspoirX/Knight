package com.lzx.compiler;

import com.lzx.annotation.KnightRouter;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


public abstract class BaseProcessor extends AbstractProcessor {

    protected Filer filer;
    protected Elements elements;
    protected Messager messager;
    protected Types types;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public boolean validateClass(TypeElement typeElement) {
        if (isSubType(typeElement, "android.app.Activity")
                && isSubType(typeElement, "androidx.fragment.app.Fragment")
                && isSubType(typeElement, "android.app.Fragment")
                && isSubType(typeElement, "android.support.v4.app.Fragment")
        ) {
            showLog(String.format("%s is not a subclass of Activity or Fragment.", typeElement.getSimpleName().toString()));
            return false;
        }
        Set<Modifier> modifiers = typeElement.getModifiers();
        // abstract class.
        if (modifiers.contains(Modifier.ABSTRACT)) {
            showLog(String.format("The class %s is abstract. You can't annotate abstract classes with @%s.",
                    (typeElement).getQualifiedName(), KnightRouter.class.getSimpleName()));
            return false;
        }
        return true;
    }

    public boolean isInterceptor(Element element) {
        return isSubType(element, "com.lzx.knight.router.intercept.AsyncInterceptor")
                || isSubType(element, "com.lzx.knight.router.intercept.SyncInterceptor");
    }

    public boolean isSubType(Element typeElement, String type) {
        return !processingEnv.getTypeUtils().isSubtype(typeElement.asType(),
                processingEnv.getElementUtils().getTypeElement(type).asType());
    }

    public void showLog(String msg) {
        System.out.println(msg);
    }
}