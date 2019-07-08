package com.wsg.retrylib

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.wsg.retry.RequestRetry
import java.util.HashMap

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val instance = RequestRetry.instance
        /**
         * 设置上传的类 必须包含UploadClass注解
         */
        instance.setUploadClass(ClassA::class.java)
        /**
         * 注册网络广播
         */
        instance.registerNetworkReceiver(this)
        /**
         * 添加消息
         */
        instance.addRequest<Any>(MachineInfo(HashMap(2)))

        instance.addRequest<Any>("dda1","uploadMachineState")
        instance.addRequest<Any>("dda2")
        instance.addRequest<Any>("dda3")
        instance.addRequest<Any>("dda4")
        instance.addRequest<Any>("dda5")
        instance.addRequest<Any>("dda6")
    }
}
