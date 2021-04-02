package com.user.impl


import com.lzx.annotation.KnightImpl
import com.user.service.IUserManager
import com.user.service.UserInfo

/**
 *  IUserManager 接口有多个实现，需要在注解中标记 register 来区分
 */
@KnightImpl(key = "UserManagerImpl")
class UserManagerImpl : IUserManager() {
    override fun getUserInfo(): UserInfo {
        return UserInfo().apply { username = "小明" }
    }
}