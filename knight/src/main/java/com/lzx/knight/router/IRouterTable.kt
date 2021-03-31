package com.lzx.knight.router

import android.util.Pair

interface IRouterTable {
    fun getRouterMap(): HashMap<String, Pair<String, String>>
}