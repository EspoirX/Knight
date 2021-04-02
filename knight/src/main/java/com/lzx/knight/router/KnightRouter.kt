package com.lzx.knight.router

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.lzx.knight.Knight
import com.lzx.knight.router.CompleteListener.Companion.CODE_ERROR
import com.lzx.knight.router.CompleteListener.Companion.CODE_FORBIDDEN
import com.lzx.knight.router.CompleteListener.Companion.CODE_NOT_FOUND
import com.lzx.knight.router.CompleteListener.Companion.CODE_SUCCESS
import com.lzx.knight.router.intercept.ISyInterceptor
import com.lzx.knight.router.intercept.interceptorSet

object KnightRouter {

    internal const val FLAG = "://"
    private var defaultScheme = "KnightRouter$FLAG"

    private var register = RouterRegister()

    init {
        try {
            val clazz = Class.forName("com.lzx.knight.router.RouterTable")
            val table = clazz.newInstance() as IRouterTable
            table.registerRouter(register)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
//
//    fun attachInterceptors(interceptors: MutableList<ISyInterceptor>) {
//        interceptorService.attachInterceptors(interceptors)
//    }
//
//    fun addInterceptor(interceptor: ISyInterceptor?) {
//        interceptorService.addInterceptor(interceptor)
//    }

    /**
     * 开始路由转跳
     */
    fun startRouter(builder: RouterBuilder) {
        val routerPath = builder.path
        //监听
        val listener = builder.getExtra(RouterBuilder.KEY_COMPLETE_LISTENER) as CompleteListener?

        val context = builder.context
        if (context == null) {
            Knight.log("router context is null")
            listener?.onError(CODE_ERROR, "router context is null")
            return
        }

        //path 参数
        val pathParams = builder.getExtra(RouterBuilder.KEY_PATH_PARAMS) as HashMap<String, String>?
        val paramMap = routerPath?.getUrlParams() ?: hashMapOf()
        if (pathParams?.isNullOrEmpty() == false) {
            paramMap.putAll(pathParams)
        }

        // scheme
        val scheme = builder.getExtra(RouterBuilder.KEY_SCHEME) as String?

        // startActivity
        val intent = Intent()

        val realScheme = getScheme(routerPath, scheme)
        val key = getRouterKey(routerPath, realScheme)
        if (key.isNullOrEmpty()) {
            Knight.log("can not find path key in the router table")
            listener?.onError(CODE_NOT_FOUND, "can not find path key in the router table")
            return
        }

        val interceptors =
            builder.getExtra(RouterBuilder.KEY_INTERCEPTOR) as MutableList<ISyInterceptor>?
        //拦截器
        interceptorSet {
            if (!interceptors.isNullOrEmpty()) {
                interceptors.forEach {
                    addInterceptor(it)
                }
            }
            if (!register.routerMap[key]?.interceptors.isNullOrEmpty()) {
                register.routerMap[key]?.interceptors?.forEach {
                    addInterceptor(it)
                }
            }
            onContinue = { context: Context, _: String, path: String?,
                           pathParam: HashMap<String, String>, intent: Intent ->
                handlerRouterParams(context, path, key, pathParam, intent, builder, listener)
            }
            onInterrupt = {
                listener?.onError(CODE_ERROR, it?.message.orEmpty())
            }
        }.doInterceptions(context, realScheme, routerPath, paramMap, intent)
    }

    private fun handlerRouterParams(
        context: Context,
        routerPath: String?,
        key: String?,
        paramMap: HashMap<String, String>,
        intent: Intent,
        builder: RouterBuilder,
        listener: CompleteListener?
    ) {
        if (routerPath.isNullOrEmpty()) {
            Knight.log("router path is empty")
            listener?.onError(CODE_ERROR, "router path is empty")
            return
        }
        // Extra
        val extras = builder.getExtra(RouterBuilder.KEY_INTENT_EXTRA) as Bundle?
        // 是否限制Intent的packageName，限制后只会启动当前App内的页面，不启动其他App的页面
        val limitPackage = builder.getExtra(RouterBuilder.KEY_LIMIT_PACKAGE) as Boolean?
        // 转跳动画
        val anim = builder.getExtra(RouterBuilder.KEY_START_ACTIVITY_ANIMATION) as IntArray?
        // startActivityForResult
        val requestCode = builder.getExtra(RouterBuilder.KEY_REQUEST_CODE) as Int?
        // options
        val options = builder.getExtra(RouterBuilder.KEY_START_ACTIVITY_OPTIONS) as Bundle?
        val map = register.routerMap
        //获取目标界面全类名
        val target = register.routerMap[key]?.className

        if (target.isNullOrEmpty()) {
            listener?.onError(CODE_NOT_FOUND, "router target is not found")
            return
        }

        var result =
            startActivityImpl(context, intent, target, extras, paramMap, requestCode, options, anim)
        if (limitPackage == true || result == CODE_SUCCESS) {
            listener?.onSuccess()
            return
        }
        // App内启动失败，再尝试启动App外页面
        intent.setPackage(null)
        result =
            startActivityImpl(context, intent, target, extras, paramMap, requestCode, options, anim)
        if (result == CODE_SUCCESS) {
            listener?.onSuccess()
        } else {
            listener?.onError(result, "")
        }
    }

    private fun startActivityImpl(
        context: Context,
        intent: Intent,
        target: String,
        extras: Bundle?,
        paramMap: HashMap<String, String>,
        requestCode: Int?,
        options: Bundle?,
        anim: IntArray?
    ): Int {
        try {
            intent.setPackage(context.packageName)   // 设置package，先尝试启动App内的页面
            intent.setClassName(context, target)
            //配置参数
            if (extras != null) {
                intent.putExtras(extras)
            }
            if (paramMap.isNotEmpty()) {
                paramMap.forEach { intent.putExtra(it.key, it.value) }
            }
            //启动
            if (requestCode != null && context is Activity) {
                ActivityCompat.startActivityForResult(context, intent, requestCode, options)
            } else {
                ActivityCompat.startActivity(context, intent, options)
            }
            //动画
            if (context is Activity && anim != null && anim.size == 2) {
                context.overridePendingTransition(anim[0], anim[1])
            }
            return CODE_SUCCESS
        } catch (ex: ActivityNotFoundException) {
            ex.printStackTrace()
            return CODE_NOT_FOUND
        } catch (ex: SecurityException) {
            ex.printStackTrace()
            return CODE_FORBIDDEN
        }
    }


    /**
     * 获取路由表中的 Key
     */
    private fun getRouterKey(path: String?, scheme: String): String? {
        if (path.isNullOrEmpty()) return null
        val realPath: String = if (!path.truncateUrlPage().isNullOrEmpty()) {
            path.substring(0, path.indexOf("?"))
        } else {
            path
        }
        return if (realPath.contains(FLAG)) { //如果path中有scheme，则进行替换
            val oldScheme = realPath.substring(0, realPath.indexOf(FLAG))
            realPath.replace(oldScheme, scheme)
        } else {
            scheme + realPath
        }
    }

    private fun getScheme(path: String?, scheme: String?): String {
        if (path.isNullOrEmpty()) return defaultScheme
        return if (scheme.isNullOrEmpty()) {
            val indexOfFlag = path.indexOf(FLAG)
            if (path.contains(FLAG)) path.substring(0, indexOfFlag) else defaultScheme
        } else {
            if (scheme.contains(FLAG)) scheme else scheme + FLAG
        }
    }


}