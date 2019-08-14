package com.wsg.retrylib

import android.app.Activity
import android.content.BroadcastReceiver
import android.os.Build

import android.os.Bundle
import android.util.Log
import com.wsg.retry.RequestRetry
import java.util.HashMap

class MainActivity : Activity() {
 var netw :BroadcastReceiver?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("序列号", "${Build.SERIAL}")
        val instance = RequestRetry.instance
        /**
         * 设置上传的类 必须包含UploadClass注解
         */
        instance.setUploadClass(UpdateClass::class.java)
        /**
         * 注册网络广播
         */
        instance.registerNetworkReceiver(this)
        /**
         * 添加消息
         */
//        instance.addRequest<Any>(MachineInfo(HashMap(2)))

        instance.addRequest<Any>("dda1", "uploadMachineState")
        instance.addRequest<Any>("dda2")
        instance.addRequest<Any>("dda3")
        instance.addRequest<Any>("dda4")
        instance.addRequest<Any>("dda5")
        instance.addRequest<Any>("dda6")
    }

    override fun onPause() {
        super.onPause()
       RequestRetry.instance.unregisterNetworkReceiver()
    }

}
