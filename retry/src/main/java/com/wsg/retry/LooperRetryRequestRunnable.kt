package com.wsg.retry


import java.lang.Exception


/**
 * 循坏发送线程
 */
class LooperRetryRequestRunnable : Runnable {
    override fun run() {
        while (true) {
            if (!(RequestRetry.instance.isTerminate)) {
                Thread.sleep(RequestRetry.instance.sleepTime)
                //有可能在休眠时间网络断开 需要重新判断
                if(!(RequestRetry.instance.isTerminate)){
                    val request = RequestRetry.instance.putRequest()
                    val re = request as RetryBean<*>
                    try {
                        re.kFunction.call(RequestRetry.instance.kClassInstance, re)
                    } catch (e: Exception) {
                        if (re.errorMutableList.size < RequestRetry.instance.retryTime) {
                            re.errorMutableList.add(e)
                            RequestRetry.instance.addRetryBean(re)
                        }
                    }
                }
            }
            Thread.sleep(100)
        }
    }


}