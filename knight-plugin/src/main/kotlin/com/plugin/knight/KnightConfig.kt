package com.plugin.knight

object KnightConfig {

    var isDebug = true

    fun showLog(msg: String) {
        if (isDebug) {
            println(msg)
        }
    }
}