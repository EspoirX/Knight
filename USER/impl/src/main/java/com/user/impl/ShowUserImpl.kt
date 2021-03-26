package com.user.impl

import android.content.Context
import android.widget.Toast
import com.lzx.knight.annotations.KnightImpl
import com.user.service.ShowUserInterface
import com.user.service.UserInfo

@KnightImpl
class ShowUserImpl : ShowUserInterface {
    override fun getUserInfo(): UserInfo? {
        return UserInfo().apply { username = "小黑" }
    }

    override fun saveUserInfo(context: Context) {
        Toast.makeText(context, "保存小黑信息", Toast.LENGTH_SHORT).show()
    }
}