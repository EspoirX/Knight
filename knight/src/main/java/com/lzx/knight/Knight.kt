package com.lzx.knight

import java.util.concurrent.ConcurrentHashMap

object Knight {

    private val mCenter: ConcurrentHashMap<String, Any?> = ConcurrentHashMap()
    private val serviceImplMap = hashMapOf<String, String>()
    private var servicesManager: IServiceManager? = null

    init {
        try {
            val clazz = Class.forName("com.lzx.knight.KnightServiceManager")
            servicesManager = clazz.newInstance() as IServiceManager
            servicesManager?.serviceImplMap?.let { serviceImplMap.putAll(it) }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * 根据clazz获取实际的实例
     */
    fun <T> of(clazz: Class<T>, register: String = "KnightDefault"): T? {
        val key = clazz.name.replace(".", "/") + "_" + register
        var knight = mCenter[key]
        if (knight == null) {
            synchronized(clazz) {
                try {
                    knight = mCenter[key]
                    if (knight == null) {
                        serviceImplMap[key]?.let {
                            val implClazz = it.replace("/", ".")
                            val impl = Class.forName(implClazz).newInstance()
                            mCenter[key] = impl
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
}