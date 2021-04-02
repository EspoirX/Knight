package com.plugin.knight

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project


class KnightPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        KnightConfig.showLog("--- register  KnightPlugin ---")
        //判断是否是 Kotlin 项目
        val isKtPro =
            project.plugins.hasPlugin("kotlin-android") || project.plugins.hasPlugin("org.jetbrains.kotlin.android")
        if (isKtPro) {
            if (!project.plugins.hasPlugin("kotlin-kapt") && !project.plugins.hasPlugin("org.jetbrains.kotlin.kapt")) {
                project.plugins.apply("kotlin-kapt")
            }
        }
        //如果是 Kotlin 项目，把 annotationProcessor 替换成 kapt
        var aptConfig = "annotationProcessor"
        if (isKtPro) {
            aptConfig = "kapt"
        }
        //自动导入相关库
        val knightPro = project.rootProject.findProject("knight")
        val compilerPro = project.rootProject.findProject("compiler")
        if (knightPro != null && compilerPro != null) {
            project.dependencies.add("api", knightPro)
            project.dependencies.add(aptConfig, compilerPro)
        }

        val appExtension = project.properties["android"] as AppExtension?
//        appExtension?.registerTransform(RouterTransform(project), Collections.EMPTY_LIST)
//        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
//            val extension = project.extensions.getByType(AppExtension::class.java)
////            extension?.registerTransform(KnightTransform(project), Collections.EMPTY_LIST)
//            extension?.registerTransform(RouterTransform(project), Collections.EMPTY_LIST)
//        }
    }
}