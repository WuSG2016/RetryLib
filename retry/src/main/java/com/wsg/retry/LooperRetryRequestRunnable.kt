package com.wsg.retry


import android.util.Log
import com.wsg.common.Logger
import java.lang.Exception


/**
 * 循坏发送线程
 */
class LooperRetryRequestRunnable : Runnable {
    private val logTag = "retryLog"
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
                            Logger.otherTagLog(msg = "执行结果-->>${result}", logTag = logTag)
                            RequestRetry.instance.retryIfException.onRetryException(
                                request,
                                null,
                                result
                            )
                        } catch (e: Exception) {
                           Logger.otherTagLog(msg = "异常信息-->$e", logTag = logTag)
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