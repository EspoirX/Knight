package com.lzx.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lzx.annotation.KnightRouter

@KnightRouter(path = ["ActivityD"], interceptors = [InterceptA::class, InterceptB::class])
class ActivityD : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showToast("ActivityD")
    }
}