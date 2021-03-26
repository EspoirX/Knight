package com.user.impl

import android.content.Context
import android.widget.Toast
import com.lzx.knight.annotations.KnightImpl
import com.user.service.BaseUserInterface
import com.user.service.UserInfo

@KnightImpl
class OtherUserInfo : BaseUserInterface {
    override fun getUserInfo(): UserInfo? {
        return UserInfo().apply { username = "小红" }
    }

    override fun saveUserInfo(context: Context) {
        Toast.makeText(context, "保存小红信息", Toast.LENGTH_SHORT).show()
    }
}