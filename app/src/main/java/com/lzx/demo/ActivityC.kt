package com.lzx.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lzx.annotation.KnightRouter

@KnightRouter(
    scheme = "XIANGE",
    path = ["ActivityC", "哈哈哈"],
    interceptors = [InterceptA::class]
)
class ActivityC : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showToast("ActivityC")
    }
}