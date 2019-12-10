package com.wsg.retry

import com.wsg.common.Logger
import java.lang.reflect.InvocationTargetException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException

class DefaultRetryIfException : RetryIfException<Exception, Any>() {
    private val logTag = "retryLog"
    private val socketExceptionSet: MutableSet<Class<*>> =mutableSetOf(SocketTimeoutException::class.java, SocketException::class.java, ConnectException::class.java)
    /**
     * 先判断重试次数 再判断异常类型
     */
    override fun <Exception, Any> onRetryException(retryBean: RetryBean<*>, e: Exception?, result: Any?): Boolean {
        return super.onRetryException(retryBean, e, result) && (return when (e) {
            is InvocationTargetException ->{
                Logger.otherTagLog(msg = "具体异常信息-->ExceptionClass:[${e.targetException::class.java}]--message[${e.targetException.message}]",logTag=logTag)
                socketExceptionSet.contains(e.targetException::class.java)}
            else -> {
                false
            }
        })


    }

}