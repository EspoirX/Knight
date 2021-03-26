package com.lzx.demo

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lzx.annoation.KnightService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn.setOnClickListener {
            //调用 User模块中 UserManagerImpl 方法
            //val info = Knight.of(IUserManager::class.java, "UserManagerImpl")?.getUserInfo()
            //showToast("nickname = " + info?.username.orEmpty())
        }
        btn1.setOnClickListener {
            //调用 User模块中 SaveUserManager 方法
            //Knight.of(IUserManager::class.java, "SaveUserManager")?.saveUserInfo(this)
        }
        btn2.setOnClickListener {
            //调用 IM 模块方法
            //Knight.of(ISendMessage::class.java)?.sendMessage(this, "大家好")
        }
    }
}

fun Context.showToast(msg: String?) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}