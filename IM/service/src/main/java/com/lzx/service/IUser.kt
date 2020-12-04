package com.lzx.service

import com.lzx.knight.annotations.KnightService

@KnightService
interface IUser {
    fun getUserInfo(): UserInfo
}