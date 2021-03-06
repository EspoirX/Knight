package com.lzx.demo

import android.content.Context
import android.content.Intent
import com.lzx.knight.router.intercept.SyncInterceptor

class InterceptA : SyncInterceptor {
    override fun process(
        context: Context,
        scheme: String,
        path: String?,
        pathParam: HashMap<String, String>,
        intent: Intent
    ): String? {
        context.showToast("--InterceptA--")
        return path
    }

    override fun getTag(): String = "ClassAIntercept"
}