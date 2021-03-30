package com.lzx.knight.router.intercept

interface InterceptorCallback {
    /**
     * 继续进行
     */
    fun onContinue(path: String?)

    /**
     * 中断，中断后会直接回调失败
     */
    fun onInterrupt(exception: Throwable?)
}