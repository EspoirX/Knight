package com.plugin.knight

import com.android.build.gradle.AppExtension
import com.plugin.knight.transform.KnightTransform
import com.plugin.knight.transform.RouterTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*


class KnightPlugin : Plugin<Project> {
    override fun apply(project: Project) {
//        KnightConfig.showLog("--- register  KnightPlugin ---")
//        val appExtension = project.properties["android"] as AppExtension?
//        appExtension?.registerTransform(KnightTransform(project), Collections.EMPTY_LIST)
//        appExtension?.registerTransform(RouterTransform(project), Collections.EMPTY_LIST)
    }
}