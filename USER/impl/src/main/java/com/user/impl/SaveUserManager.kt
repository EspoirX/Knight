package com.user.impl

import android.content.Context
import android.widget.Toast
import com.lzx.annotation.KnightImpl
import com.user.service.IUserManager

/**
 *  IUserManager 接口有多个实现，需要在注解中标记 register 来区分
 */
@KnightImpl(key = "SaveUserManager")
class SaveUserManager : IUserManager() {

    override fun saveUserInfo(context: Context) {
        Toast.makeText(context, "保存用户信息", Toast.LENGTH_SHORT).show()
    }
}