package com.lzx.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lzx.knight.annotations.KnightRouter

@KnightRouter(
    scheme = "XIANGE",
    path = ["ActivityC", "ActivityCCCC"],
    interceptors = [InterceptA::class, InterceptB::class]
)
class ActivityC : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showToast("ActivityC")
    }
}