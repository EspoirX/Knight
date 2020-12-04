package com.lzx.impl

import com.lzx.service.IPay
import com.lzx.knight.annotations.KnightImpl

@KnightImpl(register = "Apple")
class ApplePlayImpl : IPay {
    override fun pay(): String {
        return "-- ApplePlayImpl --"
    }
}