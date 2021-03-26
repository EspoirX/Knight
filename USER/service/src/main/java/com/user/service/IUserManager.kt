package com.user.service

import android.content.Context
import com.lzx.annoation.KnightService

/**
 * @KnightService 同样可以标记在抽象类上
 */
@KnightService
abstract class IUserManager : BaseUserInterface {

    /**
     * 空实现，让具体实现类自己选择方法
     */
    override fun saveUserInfo(context: Context) {
    }

    /**
     * 空实现，让具体实现类自己选择方法
     */
    override fun getUserInfo(): UserInfo? {
        return null
    }
}