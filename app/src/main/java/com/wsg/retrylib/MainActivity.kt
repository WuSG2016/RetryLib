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
        instance.setUploadClass(ClassA::class.java)
        instance.registerNetworkReceiver(this)
        val isok = instance.addRequest<Any>(MachineInfo(HashMap(2)))
        println(isok)
        instance.addRequest<Any>("dda1")
        instance.addRequest<Any>("dda2")
        instance.addRequest<Any>("dda3")
        instance.addRequest<Any>("dda4")
        instance.addRequest<Any>("dda5")
        instance.addRequest<Any>("dda6")
    }
}
