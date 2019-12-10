package com.wsg.retrylib

import com.wsg.retry.*
import java.io.IOException
import java.net.SocketTimeoutException


/**局限性
 */

@UploadClass
class UpdateClass {
    private var count = 0
    private var count1 = 0
    @ClassBean(BeanClass = "MachineInfo")
    fun uploadMachineInfo(retryBean: RetryBean<MachineInfo>) {
        count += 1
//        if (count < 5) {
//            println(2)
//            getThrowable()
//        }
        println("上传机器错误接口")
    }

    @Repetition
    @ClassBean(BeanClass = "MachineInfo")
    fun uploadMachineInfo2(retryBean: RetryBean<*>) {
        count1 += 1
        if (count1 == 2) {
            println(1)
            throw SocketTimeoutException("io ex")
        }
        if (count1 < 5) {
            println(2)
            throw IOException("io ex1")
        }
        println("上传机器错误接口2")
    }

    @ClassBean(BeanClass = "String")
    fun uploadMachineStatus(retryBean: RetryBean<*>) {
        count += 1
        if (count < 5) {
            println(32)
            try {
                getThrowable()
            } catch (e: Exception) {
                throw SocketTimeoutException("ddad")
            }
        }
        println("上传机器状态接口")
    }

    fun getThrowable() {
        throw SocketTimeoutException("get")
    }

}