package com.wsg.retry


import java.lang.Exception



/**
 * 循坏发送线程
 */
class LooperRetryRequestRunnable : Runnable {
    override fun run() {
        while (RequestRetry.instance.isTerminate) {
            val request = RequestRetry.instance.putRequest()
            val re = request as RetryBean<*>
            Thread.sleep(RequestRetry.instance.sleepTime)
            try {
                re.kFunction.call(RequestRetry.instance.kClassInstance, re)
            } catch (e: Exception) {
                if (re.errorMutableList.size < RequestRetry.instance.retryTime ) {
                    re.errorMutableList.add(e)
                    RequestRetry.instance.addRetryBean(re)
                }
            }
        }
    }


}