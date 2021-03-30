package com.lzx.knight

import android.content.Context
import android.util.Log
import com.lzx.knight.router.RouterBuilder
import java.util.concurrent.ConcurrentHashMap

object Knight {

    private val mCenter: ConcurrentHashMap<String, Any?> = ConcurrentHashMap()
    private val serviceMap = hashMapOf<String, String>()
    private var servicesManager: IServiceManager? = null

    init {
        try {
            val clazz = Class.forName("com.lzx.knight.KnightServiceManager")
            servicesManager = clazz.newInstance() as IServiceManager
            servicesManager?.serviceMap?.let { serviceMap.putAll(it) }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * 根据clazz获取实际的实例
     */
    fun <T> of(clazz: Class<T>, key: String = ""): T? {
        val suffix = if (key.isEmpty()) "" else "_$key"
        val classKey = clazz.name.replace(".", "/") + suffix
        var knight = mCenter[classKey]
        if (knight == null) {
            synchronized(clazz) {
                try {
                    knight = mCenter[classKey]
                    if (knight == null) {
                        serviceMap[classKey]?.let {
                            val implClazz = it.replace("/", ".")
                            val impl = Class.forName(implClazz).newInstance()
                            mCenter[classKey] = impl
                            knight = impl
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return null
                }
            }
        }
        return knight as T?
    }

    fun router(context: Context?, path: String?): RouterBuilder {
        return RouterBuilder(context, path)
    }

    fun log(msg: String) {
        Log.i("Knight", msg)
    }
}