package com.wsg.retrylib

import com.wsg.retry.RequestRetry


/**
 * 数据类
 */
data class MachineInfo(val hashMap: HashMap<String, String>)

fun main() {
    with(RequestRetry.instance) {
        setUploadClass(UpdateClass::class.java)
        addRequest(MachineInfo(HashMap()), "uploadMachineInfo2")
    }

}