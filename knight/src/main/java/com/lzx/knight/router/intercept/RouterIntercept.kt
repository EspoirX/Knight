package com.lzx.knight.router.intercept

import android.content.Context
import android.content.Intent

/**
 * 拦截器
 */
interface ISyInterceptor {

    fun getTag(): String

    fun process(
        context: Context,
        scheme: String,
        path: String?,
        pathParam: HashMap<String, String>,
        intent: Intent,
        callback: InterceptorCallback
    )

    fun process(
        context: Context,
        scheme: String,
        path: String?,
        pathParam: HashMap<String, String>,
        intent: Intent
    ): String?
}

abstract class AsyncInterceptor : ISyInterceptor {
    override fun process(
        context: Context,
        scheme: String,
        path: String?,
        pathParam: HashMap<String, String>,
        intent: Intent
    ): String? = ""  //do nothing

    override fun getTag(): String = this.javaClass.name
}

interface SyncInterceptor : ISyInterceptor {
    override fun process(
        context: Context,
        scheme: String,
        path: String?,
        pathParam: HashMap<String, String>,
        intent: Intent,
        callback: InterceptorCallback
    ) {
        //do nothing
    }

    override fun getTag(): String = this.javaClass.name
}