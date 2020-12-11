package com.plugin.knight

import com.android.build.gradle.AppExtension
import com.plugin.knight.transform.KnightTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*


class KnightPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val appExtension = project.properties["android"] as AppExtension?

//        project.extensions.create("KnightConfig", KnightExtension::class.java)
//        project.afterEvaluate {
//            val extension = project.extensions.getByType(KnightExtension::class.java)
//            KnightConfig.debugSwitch = extension.debugSwitch
//        }

        appExtension?.registerTransform(KnightTransform(project), Collections.EMPTY_LIST)
    }
}