package com.lzx.knight.router

interface CompleteListener {
    companion object {
        //跳转成功
        var CODE_SUCCESS = 200

        //重定向到其他URI，会再次跳转
        var CODE_REDIRECT = 301

        //请求错误，通常是Context或URI为空
        var CODE_BAD_REQUEST = 400

        //权限问题，通常是外部跳转时Activity的exported=false
        var CODE_FORBIDDEN = 403

        //找不到目标
        var CODE_NOT_FOUND = 404

        //发生其他错误
        var CODE_ERROR = 500
    }

    //转跳成功
    fun onSuccess()

    //转跳失败
    fun onError(resultCode: Int, msg: String)
}