package com.wsg.retry

import java.lang.reflect.InvocationTargetException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException

class DefaultRetryIfException : RetryIfException<Exception, Any>() {

    private val socketExceptionSet: MutableSet<Class<*>> =
        mutableSetOf(SocketTimeoutException::class.java, SocketException::class.java, ConnectException::class.java)

    override fun <Exception, Any> onRetryException(e: Exception?, result: Any?): Boolean {
        println("onRetryException")
        if(e==null){
            println("e==null")
        }
        //增加异常类型重试
        if (e is InvocationTargetException) {
            val exception = e.targetException
             print(e.targetException::class.java)
            if (socketExceptionSet.contains(exception::class.java)) {
                return true
            }
        }
        return false
    }

}