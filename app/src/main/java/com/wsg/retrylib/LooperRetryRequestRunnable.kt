package com.wsg.retrylib

import com.retail.newretail.vmc.RequestRetry
import com.retail.newretail.vmc.RetryBean

/**
 * 循坏发送线程
 */
class LooperRetryRequestRunnable : Runnable {
    override fun run() {
        startRun()
    }

    /**
     * 开始执行
     */
    private fun startRun() {
        while (RequestRetry.instance.isTerminate) {
            val request = RequestRetry.instance.putRequest()
            val re = request as RetryBean<*>
            re.kFunction.call(RequestRetry.instance.kClassInstance, re)
            Thread.sleep(RequestRetry.instance.retryTimes)
        }
    }


}