package com.lzx.knight.router.intercept

import android.content.Intent

/**
 * 拦截器
 */
interface ISyInterceptor {

    fun getTag(): String

    /**
     * 这个方法运行在子线程
     */
    fun process(
        path: String?,
        pathParam: HashMap<String, String>,
        intent: Intent,
        callback: InterceptorCallback
    )

    /**
     * 这个方法运行在主线程
     */
    fun process(path: String?, pathParam: HashMap<String, String>, intent: Intent): String?
}

abstract class AsyncInterceptor : ISyInterceptor {
    override fun process(
        path: String?,
        pathParam: HashMap<String, String>,
        intent: Intent
    ): String {
        //do nothing
        return ""
    }
}

interface SyncInterceptor : ISyInterceptor {
    override fun process(
        path: String?,
        pathParam: HashMap<String, String>,
        intent: Intent,
        callback: InterceptorCallback
    ) {
        //do nothing
    }
}