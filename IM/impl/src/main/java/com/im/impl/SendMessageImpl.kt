package com.im.impl

import android.content.Context
import android.widget.Toast
import com.im.service.ISendMessage
import com.lzx.knight.Knight
import com.lzx.annoation.KnightImpl
import com.user.service.IUserManager

@com.lzx.annoation.KnightImpl
class SendMessageImpl : ISendMessage {
    override fun sendMessage(context: Context, msg: String) {
        //获取User模块的用户信息
        val userInfo = Knight.of(IUserManager::class.java, "UserManagerImpl")?.getUserInfo()

        val textMsg = "用户 " + userInfo?.username + " 发消息：" + msg
        Toast.makeText(context, textMsg, Toast.LENGTH_SHORT).show()
    }
}