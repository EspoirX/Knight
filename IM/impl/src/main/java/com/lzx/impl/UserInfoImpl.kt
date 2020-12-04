package com.lzx.impl

import com.lzx.service.IUser
import com.lzx.service.UserInfo
import com.lzx.knight.annotations.KnightImpl

@KnightImpl
class UserInfoImpl : IUser {
    override fun getUserInfo(): UserInfo {
        val info = UserInfo()
        info.username = "你大爷"
        return info
    }
}