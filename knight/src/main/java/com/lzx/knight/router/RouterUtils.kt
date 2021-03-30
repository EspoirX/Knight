package com.lzx.knight.router

import java.io.UnsupportedEncodingException
import java.net.URLDecoder

object RouterUtils {

    fun String?.getUrlParams(): HashMap<String, String> {
        val mapRequest = hashMapOf<String, String>()
        if (this.isNullOrEmpty()) return mapRequest
        val strUrlParam = this.truncateUrlPage()
        if (strUrlParam.isNullOrEmpty()) {
            return mapRequest
        }
        // 每个键值为一组
        val arrSplit = strUrlParam.split("[&]".toRegex()).toTypedArray()
        for (strSplit in arrSplit) {
            val arrSplitEqual = strSplit.split("[=]".toRegex()).toTypedArray()
            if (arrSplitEqual.size > 1) {   // 解析出键值
                try {
                    mapRequest[arrSplitEqual[0]] = URLDecoder.decode(arrSplitEqual[1], "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
            } else {
                if (arrSplitEqual[0].isNotEmpty()) {
                    // 只有参数没有值，不加入
                    mapRequest[arrSplitEqual[0]] = ""
                }
            }
        }
        return mapRequest
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     * @param strURL url地址
     * @return url请求参数部分
     */
    fun String.truncateUrlPage(): String? {
        var strAllParam: String? = null
        val index = this.indexOf("?")
        if (index > 0 && index < this.length) {
            strAllParam = this.substring(index + 1, this.length)
        }
        return strAllParam
    }
}