package com.wsg.retrylib

import android.app.Activity
import android.content.BroadcastReceiver
import android.os.Build

import android.os.Bundle
import android.util.Log
import com.wsg.retry.RequestRetry
import java.util.HashMap

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("序列号", "${Build.SERIAL}")
        val instance = RequestRetry.instance
            .setRetryTime(3)
            .setUploadClass(UpdateClass::class.java)
            .registerNetworkReceiver(this)

        instance.addRequest<Any>("dda1", "uploadMachineStatus")
//        instance.addRequest<Any>("dda2", "uploadMachineStatus")
//        instance.addRequest<Any>("dda3", "uploadMachineStatus")
//        instance.addRequest<Any>("dda4", "uploadMachineStatus")
//        val map = HashMap<String, String>()
//        map["dad"] = "dada"
//        val map1 = HashMap<String, String>()
//        map1["dad"] = "dada"
//        val machineInfo = MachineInfo(map)
//        val machineInfo1 = MachineInfo(map1)
//        instance.addRequest<Any>("dda2")
//        instance.addRequest(machineInfo)
    }

    override fun onPause() {
        super.onPause()
        RequestRetry.instance.unregisterNetworkReceiver()
    }

}
