package com.lzx.knight.router

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.lzx.knight.Knight
import com.lzx.knight.router.RouterUtils.getUrlParams
import com.lzx.knight.router.RouterUtils.truncateUrlPage
import java.util.concurrent.ConcurrentHashMap

object KnightRouter {

    internal const val FLAG = "://"
    private var defaultScheme = "KnightRouter$FLAG"

    private val routerCache: ConcurrentHashMap<String, String> = ConcurrentHashMap()
    private var routerMap = hashMapOf<String, String>()

    init {
        try {
            val clazz = Class.forName("com.lzx.knight.router.RouterTable")
            val table = clazz.newInstance() as IRouterTable?
            if (table != null) {
                routerMap = table.getRouterMap()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * 获取目标界面全类名
     */
    private fun getRouterTarget(path: String, scheme: String?): String? {
        val realPath: String = if (!path.truncateUrlPage().isNullOrEmpty()) {
            path.substring(0, path.indexOf("?"))
        } else {
            path
        }
        val key: String = if (scheme.isNullOrEmpty()) {
            if (realPath.contains(FLAG)) realPath else defaultScheme + realPath
        } else {
            if (realPath.contains(FLAG)) {
                val startIndex = realPath.indexOf(FLAG) + 3
                val pathWithoutScheme = realPath.substring(startIndex, realPath.length)
                scheme + pathWithoutScheme
            } else {
                scheme + realPath
            }
        }
        var target = routerCache[key]
        if (target.isNullOrEmpty()) {
            synchronized(realPath) {
                try {
                    target = routerCache[key]
                    if (target.isNullOrEmpty()) {
                        routerMap[key]?.let {
                            val realTarget = it.replace("/", ".")
                            routerCache[key] = realTarget
                            target = realTarget
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return ""
                }
            }
        }
        return target
    }

    fun startRouter(builder: RouterBuilder) {
        val routerPath = builder.path

        //监听
        val listener = builder.getExtra(RouterBuilder.KEY_COMPLETE_LISTENER) as CompleteListener?

        if (routerPath.isEmpty()) {
            Knight.log("router path is empty")
            listener?.onError(CompleteListener.CODE_ERROR, "router path is empty")
            return
        }
        val context = builder.context
        if (context == null) {
            Knight.log("router context is null")
            listener?.onError(CompleteListener.CODE_ERROR, "router context is null")
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
        // scheme
        val scheme = builder.getExtra(RouterBuilder.KEY_SCHEME) as String?
        // options
        val options = builder.getExtra(RouterBuilder.KEY_START_ACTIVITY_OPTIONS) as Bundle?
        //获取url参数，只会显示在
        val paramMap = routerPath.getUrlParams()
        //获取目标界面全类名
        val target = getRouterTarget(routerPath, scheme)
        if (target.isNullOrEmpty()) {
            listener?.onError(CompleteListener.CODE_NOT_FOUND, "router target is not found")
            return
        }

        //构建 Intent
        val intent = Intent()
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
        listener?.onSuccess()
    }


}