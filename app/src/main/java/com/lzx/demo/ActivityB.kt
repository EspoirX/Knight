package com.lzx.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lzx.annotation.KnightRouter

@KnightRouter(scheme = "大胸萝莉", path = ["ActivityB"])
class ActivityB : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showToast("ActivityB")
    }
}