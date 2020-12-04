package com.lzx.demo

import com.lzx.service.IPay
import com.lzx.knight.annotations.KnightImpl

@KnightImpl
class DefaultPayImpl : IPay {
    override fun pay(): String {
        return "--DefaultPayImpl--"
    }
}