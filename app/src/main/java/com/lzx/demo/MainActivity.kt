package com.lzx.demo

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lzx.service.IPay
import com.lzx.service.IUser
import com.lzx.knight.Knight
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn.setOnClickListener {
            //调用 app 模块方法
            showToast(Knight.of(IPay::class.java)?.pay())
        }
        btn1.setOnClickListener {
            //调用 moduleA 模块方法
            showToast(Knight.of(IPay::class.java, "Apple")?.pay())
        }
        btn2.setOnClickListener {
            //调用 moduleB 模块方法
            showToast(Knight.of(IPay::class.java, "WeChat")?.pay())
        }

        btn3.setOnClickListener {

        }
        btn4.setOnClickListener {

        }
        btn5.setOnClickListener {

        }

        btn6.setOnClickListener {
            val info = Knight.of(IUser::class.java)?.getUserInfo()
            showToast(info?.username.orEmpty())
        }
    }
}

fun Context.showToast(msg: String?) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}