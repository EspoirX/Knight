package com.lzx.knight.router.intercept

import android.content.Context
import android.content.Intent

fun interceptorSet(creation: InterceptorService.() -> Unit) = InterceptorService().apply {
    creation()
}

class InterceptorService {

    internal var onContinue: ((context: Context, scheme: String, path: String?, pathParam: HashMap<String, String>, intent: Intent) -> Unit)? =
        null

    internal var onInterrupt: ((exception: Throwable?) -> Unit)? = null

    private var interceptors = mutableListOf<ISyInterceptor>()

    fun attachInterceptors(interceptors: MutableList<ISyInterceptor>?) {
        interceptors?.let {
            this.interceptors.clear()
            this.interceptors.addAll(it)
        }
    }

    fun addInterceptor(interceptor: ISyInterceptor?) {
        interceptor?.let { this.interceptors.add(it) }
    }

    fun doInterceptions(
        context: Context,
        scheme: String,
        path: String?,
        pathParam: HashMap<String, String>,
        intent: Intent,
    ) {
        if (interceptors.isNullOrEmpty()) {
            onContinue?.invoke(context, scheme, path, pathParam, intent)
            return
        }
        try {
            doNextInterceptor(0, context, scheme, path, pathParam, intent)
        } catch (ex: Exception) {
            ex.printStackTrace()
            onInterrupt?.invoke(ex)
            interceptors.clear()
        }
    }

    private fun doNextInterceptor(
        index: Int,
        context: Context,
        scheme: String,
        path: String?,
        pathParam: HashMap<String, String>,
        intent: Intent
    ) {
        if (index < interceptors.size) {
            val interceptor = interceptors[index]
            if (interceptor is SyncInterceptor) {
                val result = interceptor.process(context, scheme, path, pathParam, intent)
                //执行下一个
                doNextInterceptor(index + 1, context, scheme, result, pathParam, intent)
            }
            if (interceptor is AsyncInterceptor) {
                //执行下一个
                interceptor.process(context, scheme, path, pathParam, intent,
                    object : InterceptorCallback {
                        override fun onContinue(
                            context: Context,
                            scheme: String,
                            path: String?,
                            pathParam: HashMap<String, String>,
                            intent: Intent
                        ) {
                            doNextInterceptor(
                                index + 1, context, scheme,
                                path, pathParam, intent
                            )  //执行下一个
                        }

                        override fun onInterrupt(exception: Throwable?) {
                            onInterrupt?.invoke(exception)
                        }
                    })
            }
        } else {
            onContinue?.invoke(context, scheme, path, pathParam, intent)
        }
    }
}