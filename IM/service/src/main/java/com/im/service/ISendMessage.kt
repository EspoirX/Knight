package com.im.service

import android.content.Context
import com.lzx.annotation.KnightService

@KnightService
interface ISendMessage {
    // IM 模块向外提供的发消息方法
    fun sendMessage(context: Context, msg: String)
}