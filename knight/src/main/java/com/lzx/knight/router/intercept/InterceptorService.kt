package com.lzx.knight.router.intercept

import android.content.Intent

class InterceptorService {

    private var interceptors = mutableListOf<ISyInterceptor>()

    fun attachInterceptors(interceptors: MutableList<ISyInterceptor>) {
        this.interceptors.clear()
        this.interceptors.addAll(interceptors)
    }

    fun addInterceptor(interceptor: ISyInterceptor?) {
        interceptor?.let { this.interceptors.add(it) }
    }

    fun doInterceptions(
        path: String?,
        pathParam: HashMap<String, String>,
        intent: Intent,
        callback: InterceptorCallback?
    ) {
        if (interceptors.isNullOrEmpty()) {
            callback?.onContinue(path)
            return
        }
        try {
            doNextInterceptor(0, path, pathParam, intent, callback)
        } catch (ex: Exception) {
            ex.printStackTrace()
            callback?.onInterrupt(ex)
            interceptors.clear()
        }
    }

    private fun doNextInterceptor(
        index: Int,
        path: String?,
        pathParam: HashMap<String, String>,
        intent: Intent,
        callback: InterceptorCallback?
    ) {
        if (index < interceptors.size) {
            val interceptor = interceptors[index]
            if (interceptor is SyncInterceptor) {
                val result = interceptor.process(path, pathParam, intent)
                doNextInterceptor(index + 1, result, pathParam, intent, callback) //执行下一个
            } else if (interceptor is AsyncInterceptor) {
                interceptor.process(path, pathParam, intent, object : InterceptorCallback {
                    override fun onContinue(path: String?) {
                        doNextInterceptor(index + 1, path, pathParam, intent, callback)  //执行下一个
                    }

                    override fun onInterrupt(exception: Throwable?) {
                        callback?.onInterrupt(exception)
                    }
                })
            }
        } else {
            callback?.onContinue(path)
            interceptors.clear()
        }
    }
}