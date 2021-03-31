package com.plugin.knight

object KnightConfig {

    var isDebug = true

    fun showLog(msg: String) {
        if (isDebug) {
            println(msg)
        }
    }

    fun shouldProcessClass(name: String?): Boolean {
        return !(name.isNullOrEmpty() ||
                name.contains("META-INF") ||
                name.contains("R\$") ||
                name.endsWith("R.class") ||
                name.endsWith("BuildConfig.class") ||
                name.endsWith("Knight.class") ||
                name.endsWith("KnightImpl.class") ||
                name.endsWith("KnightService.class") ||
                name.startsWith("kotlinx") ||
                name.startsWith("kotlin") ||
                name.startsWith("com/google/android") ||
                name.startsWith("android/support") ||
                name.startsWith("com.google.android") ||
                name.startsWith("android.support") ||
                name.startsWith("org") ||
                name.startsWith("androidx"))
    }
}