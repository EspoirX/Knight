package com.lzx.knight.router

import android.content.Context
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import com.lzx.knight.router.KnightRouter.FLAG
import com.lzx.knight.router.intercept.ISyInterceptor
import java.io.Serializable

class RouterBuilder(internal val context: Context?, internal val path: String?) {

    companion object {
        private const val PKG = "com.lzx.knight.router."

        internal const val KEY_SCHEME: String = PKG + "scheme"
        internal const val KEY_PATH_PARAMS: String = PKG + "path_params"
        internal const val KEY_INTENT_EXTRA: String = PKG + "intent_getBundle"
        internal const val KEY_REQUEST_CODE: String = PKG + "request_code"
        internal const val KEY_START_ACTIVITY_OPTIONS: String = PKG + "options"
        internal const val KEY_START_ACTIVITY_ANIMATION: String = PKG + "animation"
        internal const val KEY_LIMIT_PACKAGE: String = PKG + "limit_package"
        internal const val KEY_COMPLETE_LISTENER = "com.lzx.knight.router.CompleteListener"
    }

    private val keyMap = hashMapOf<String, Any>()
    private val pathParams = hashMapOf<String, String>()

    @Synchronized
    private fun getBundle(): Bundle {
        var bundle = keyMap[KEY_INTENT_EXTRA] as Bundle?
        if (bundle == null) {
            bundle = Bundle()
            keyMap[KEY_INTENT_EXTRA] = bundle
        }
        return bundle
    }

    fun getExtra(key: String): Any? {
        return keyMap[key]
    }

    fun putExtra(name: String, value: Serializable?) = apply {
        getBundle().putSerializable(name, value)
    }

    fun putExtra(name: String, value: BooleanArray?) = apply {
        getBundle().putBooleanArray(name, value)
    }

    fun putExtra(name: String, value: ByteArray?) = apply {
        getBundle().putByteArray(name, value)
    }

    fun putExtra(name: String, value: ShortArray?) = apply {
        getBundle().putShortArray(name, value)
    }

    fun putExtra(name: String, value: CharArray?) = apply {
        getBundle().putCharArray(name, value)
    }

    fun putExtra(name: String, value: IntArray?) = apply {
        getBundle().putIntArray(name, value)
    }

    fun putExtra(name: String, value: LongArray?) = apply {
        getBundle().putLongArray(name, value)
    }

    fun putExtra(name: String, value: FloatArray?) = apply {
        getBundle().putFloatArray(name, value)
    }

    fun putExtra(name: String, value: DoubleArray?) = apply {
        getBundle().putDoubleArray(name, value)
    }

    fun putExtra(name: String, value: Array<String>?) = apply {
        getBundle().putStringArray(name, value)
    }

    fun putExtra(name: String, value: Array<CharSequence>?) = apply {
        getBundle().putCharSequenceArray(name, value)
    }

    fun putExtra(name: String, value: Bundle?) = apply {
        getBundle().putBundle(name, value)
    }

    fun putExtras(bundle: Bundle?) = apply {
        getBundle().putAll(bundle)
    }

    fun activityRequestCode(requestCode: Int) = apply {
        keyMap[KEY_REQUEST_CODE] = requestCode
    }

    fun overridePendingTransition(enterAnim: Int, exitAnim: Int) = apply {
        keyMap[KEY_START_ACTIVITY_ANIMATION] = intArrayOf(enterAnim, exitAnim)
    }

    fun setScheme(scheme: String) = apply {
        val realScheme = if (scheme.endsWith(FLAG)) scheme else scheme + FLAG
        keyMap[KEY_SCHEME] = realScheme
    }

    fun addPathParam(key: String, value: String) = apply {
        var pathParams = keyMap[KEY_PATH_PARAMS] as HashMap<String, String>?
        if (pathParams == null) {
            pathParams = hashMapOf()
            keyMap[KEY_PATH_PARAMS] = pathParams
        }
        pathParams[key] = value
    }

    fun attachInterceptors(interceptors: MutableList<ISyInterceptor>) = apply {
        KnightRouter.attachInterceptors(interceptors)
    }

    fun addInterceptor(interceptor: ISyInterceptor?) = apply {
        KnightRouter.addInterceptor(interceptor)
    }


    fun limitPackage(limit: Boolean) = apply {
        keyMap[KEY_LIMIT_PACKAGE] = limit
    }

    @RequiresApi(16)
    fun setActivityOptionsCompat(options: ActivityOptionsCompat?) = apply {
        options?.toBundle()?.let {
            keyMap[KEY_START_ACTIVITY_OPTIONS] = it
        }
    }

    fun setCompleteListener(listener: CompleteListener) = apply {
        keyMap[KEY_COMPLETE_LISTENER] = listener
    }

    fun start() {
        KnightRouter.startRouter(this)
    }

}