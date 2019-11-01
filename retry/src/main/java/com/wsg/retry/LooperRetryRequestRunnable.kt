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
                if (!(RequestRetry.instance.isTerminate)) {
                    val request = RequestRetry.instance.putRequest()
                    if (request != null) {
                        val isAddMessage = try {
                            val result = request.kFunction.call(
                                RequestRetry.instance.kClassInstance,
                                request
                            )
                            RequestRetry.instance.retryIfException.onRetryException(
                                request,
                                null,
                                result
                            )
                        } catch (e: Exception) {
                            RequestRetry.instance.retryIfException.onRetryException(
                                request,
                                e,
                                null
                            )
                        }
                        if (isAddMessage) {
                            request.retryCount += 1
                            RequestRetry.instance.addRetryBean(request)
                        }
                    }
                }
            }
            Thread.sleep(100)
        }
    }


}