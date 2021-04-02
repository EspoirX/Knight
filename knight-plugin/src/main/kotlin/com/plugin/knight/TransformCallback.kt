package com.plugin.knight

interface DeleteCallBack {
    fun delete(className: String?, classBytes: ByteArray?)
}

interface ChangeCallBack {
    fun process(className: String?)
}
