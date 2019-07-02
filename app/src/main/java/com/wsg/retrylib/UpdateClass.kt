package com.wsg.retrylib

import com.retail.newretail.vmc.RetryBean

/**局限性
 * 1.没有重试的次数
 * 2.classBean相同 如何实现到底调用哪个方法
 */

@UploadClass
class UpdateClass {

    @ClassBean(name = MachineInfo::class)
    fun uploadMachineInfo(retryBean: RetryBean<*>) {
        println("上传机器错误接口")
    }
    @Repetition
    @ClassBean(name = MachineInfo::class)
    fun uploadMachineInfo2(retryBean: RetryBean<*>) {
        println("上传机器错误接口2")
    }

    @ClassBean(name = String::class)
    fun uploadMachineStatus(retryBean: RetryBean<*>) {
        println("上传机器状态接口")
    }

    fun isok(){

    }
}