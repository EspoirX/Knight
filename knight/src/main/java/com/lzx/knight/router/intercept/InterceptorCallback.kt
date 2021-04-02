package com.lzx.knight.router.intercept

import android.content.Context
import android.content.Intent

interface InterceptorCallback {
    /**
     * 继续进行
     */
    fun onContinue(
        context: Context,
        scheme: String,
        path: String?,
        pathParam: HashMap<String, String>,
        intent: Intent
    )

    /**
     * 中断，中断后会直接回调失败
     */
    fun onInterrupt(exception: Throwable?)
}