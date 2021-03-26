package com.user.service

import android.content.Context
import com.lzx.annoation.KnightService

/**
 * 用户信息基础接口
 */
@KnightService
interface BaseUserInterface {
    fun getUserInfo(): UserInfo?

    fun saveUserInfo(context: Context)
}