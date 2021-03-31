package com.lzx.demo

import android.content.Intent
import com.lzx.knight.router.intercept.SyncInterceptor

class InterceptB : SyncInterceptor {
    override fun process(
        path: String?,
        pathParam: HashMap<String, String>,
        intent: Intent
    ): String? {
        return path
    }

    override fun getTag(): String = "ClassAIntercept"
}