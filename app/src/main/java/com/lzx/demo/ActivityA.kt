package com.lzx.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lzx.annotation.KnightRouter

@KnightRouter(path = ["ActivityA"])
class ActivityA : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showToast("ActivityA，参数 = " + intent.getStringExtra("name"))
    }
}