package com.lzx.moduleb

import com.lzx.service.IPay
import com.lzx.service.IUser
import com.lzx.knight.Knight
import com.lzx.knight.annotations.KnightImpl

@KnightImpl(register = "WeChat")
class WeChatPlayImpl : IPay {
    override fun pay(): String {
        //调用 moduleA 模块方法

        val info = Knight.of(IUser::class.java)?.getUserInfo()
        return info?.username.orEmpty()
    }

}