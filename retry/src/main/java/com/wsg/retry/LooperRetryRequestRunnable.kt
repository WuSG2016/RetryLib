package com.wsg.retry


import java.lang.Exception
import java.lang.reflect.InvocationTargetException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException


/**
 * 循坏发送线程
 */
class LooperRetryRequestRunnable : Runnable {

    override fun run() {
        while (true) {
            if (!(RequestRetry.instance.isTerminate)) {
                Thread.sleep(RequestRetry.instance.sleepTime)
                //有可能在休眠时间网络断开 需要重新判断
                if (!(RequestRetry.instance.isTerminate)) {
                    val request = RequestRetry.instance.putRequest()
                    val re = request as RetryBean<*>
                    try {
                        val result = re.kFunction.call(RequestRetry.instance.kClassInstance, re)
                        val isAdd = RequestRetry.instance.retryIfException.onRetryException(null, result)
                        if (isAdd) {
                            RequestRetry.instance.addRetryBean(re)
                        }
                    } catch (e: Exception) {
                        println(e)
                        val isAdd = RequestRetry.instance.retryIfException.onRetryException(e, null)
                        if (isAdd) {
                            RequestRetry.instance.addRetryBean(re)
                        }
                    }
                }
            }
            Thread.sleep(100)
        }
    }


}