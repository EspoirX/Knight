package com.lzx.compiler;

import com.google.auto.service.AutoService;
import com.lzx.annoation.KnightImpl;
import com.lzx.annoation.KnightService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
//@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class KnightProcessor extends BaseProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedTypes = new HashSet<>();
        supportedTypes.add(KnightImpl.class.getCanonicalName());
        supportedTypes.add(KnightService.class.getCanonicalName());
        return supportedTypes;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {

        Set<? extends Element> serviceSet = roundEnv.getElementsAnnotatedWith(KnightService.class);
        Set<? extends Element> implSet = roundEnv.getElementsAnnotatedWith(KnightImpl.class);
        handlerKnightService(serviceSet);
        handlerKnightImpl(implSet);
        return false;
    }

    /**
     * 处理 KnightService 注解
     */
    private void handlerKnightService(Set<? extends Element> serviceSet) {
        for (Element element : serviceSet) {
//            showLog(" = "+serviceSet);
//            showLog("==============");
            TypeElement typeElement = (TypeElement) element;
            KnightService knightService = typeElement.getAnnotation(KnightService.class);
            if (knightService == null) {
                continue;
            }
            //获取实现的接口
            List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
            if (interfaces != null && !interfaces.isEmpty()) {
                //处理有继承关系的情况
                for (TypeMirror mirror : interfaces) {
                    if (mirror == null) {
                        continue;
                    }
                    String className = typeElement.getQualifiedName().toString();
                    RelationshipList.getInstance().serviceList.add(className);
                }
            } else {
                String className = typeElement.getQualifiedName().toString();
                RelationshipList.getInstance().serviceList.add(className);
            }
        }
    }

    /**
     * 处理 KnightImpl 注解
     */
    private void handlerKnightImpl(Set<? extends Element> serviceSet) {
        for (Element element : serviceSet) {
            TypeElement typeElement = (TypeElement) element;
            KnightImpl knightImpl = typeElement.getAnnotation(KnightImpl.class);
            if (knightImpl == null) {
                continue;
            }
            String className = typeElement.getQualifiedName().toString();
            if (!typeElement.getKind().isInterface()) {
                //获取实现的接口
                List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
                //获取继承的类
                TypeMirror superclass = typeElement.getSuperclass();
                showLog("superclass = " + superclass.toString() + " serviceList = " + RelationshipList.getInstance().serviceList.toString());
                if (RelationshipList.getInstance().serviceList.contains(superclass.toString())) {
                    RelationshipList.getInstance().serviceImplMap.put(superclass.toString(), className);
                } else if (interfaces != null && !interfaces.isEmpty()) {
                    for (TypeMirror mirror : interfaces) {
                        if (mirror == null) {
                            continue;
                        }
                        showLog("mirror = " + mirror.toString() + " serviceList = " + RelationshipList.getInstance().serviceList.toString());
                        if (RelationshipList.getInstance().serviceList.contains(mirror.toString())) {
                            RelationshipList.getInstance().serviceImplMap.put(mirror.toString(), className);
                        }
                    }
                }
            } else {
                throw new RuntimeException("标注了" + KnightImpl.class.getName() + " 注解的类 " + className + " " + "不能是接口");
            }
        }
        for (Map.Entry<String, String> entry : RelationshipList.getInstance().serviceImplMap.entrySet()) {
            showLog("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        //showLog("-------------------------------------------------");
    }

}