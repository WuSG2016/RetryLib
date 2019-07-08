package com.wsg.retry

import java.lang.Exception

/**
 * 增加异常重试类型
 */
open class RetryIfException<T : Exception, E : Any> {
    /**
     * 重试条件
     * e为方法的返回值
     * true 进行重试
     */
    open fun <T, E> onRetryException(retryBean: RetryBean<*>, t: T?, result: E?): Boolean {
        if (retryBean.retryCount >= RequestRetry.instance.retryTime) {
            return false
        }
        return true
    }
}