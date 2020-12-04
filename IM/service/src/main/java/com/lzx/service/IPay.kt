package com.lzx.service

import com.lzx.knight.annotations.KnightService

@KnightService
interface IPay {
    fun pay(): String
}