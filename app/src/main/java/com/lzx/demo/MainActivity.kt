package com.lzx.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import com.im.service.ISendMessage
import com.lzx.knight.Knight
import com.lzx.knight.router.CompleteListener
import com.lzx.knight.router.intercept.SyncInterceptor
//import com.user.service.IUserManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        btn.setOnClickListener {
//            //调用 User模块中 UserManagerImpl 方法
//            val info = Knight.of(IUserManager::class.java, "UserManagerImpl")?.getUserInfo()
//            showToast("nickname = " + info?.username.orEmpty())
//        }
//        btn1.setOnClickListener {
//            //调用 User模块中 SaveUserManager 方法
//            Knight.of(IUserManager::class.java, "SaveUserManager")?.saveUserInfo(this)
//        }
//        btn2.setOnClickListener {
//            //调用 IM 模块方法
//            Knight.of(ISendMessage::class.java)?.sendMessage(this, "大家好")
//        }

        btnA?.setOnClickListener {
            Knight.router(this, "ActivityA")
                .addPathParam("name", "102")
                .addInterceptor(object : SyncInterceptor {
                    override fun process(
                        context: Context,
                        scheme: String,
                        path: String?,
                        pathParam: HashMap<String, String>,
                        intent: Intent
                    ): String? {
                        showToast("拦截器 path=" + path + " pathParam = " + pathParam.size)
                        return path
                    }

                    override fun getTag(): String = "123"
                })
                .start()
        }
        btnB?.setOnClickListener {
            Knight.router(this, "ActivityB")
                .setScheme("大胸萝莉")
                .addPathParam("name", "103")
                .addPathParam("sex", "das")
                .start()
        }
        btnC?.setOnClickListener {
            Knight.router(this, "ActivityC")
                .setScheme("XIANGE")
                .start()
        }
        btnD?.setOnClickListener {
            Knight.router(this, "KnightRouter://ActivityD")
                .setCompleteListener(object : CompleteListener {
                    override fun onSuccess() {
                        showToast("转跳成功")
                    }

                    override fun onError(resultCode: Int, msg: String) {
                        showToast("转跳失败")
                    }
                })
                .start()
        }
    }
}

fun Context.showToast(msg: String?) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}