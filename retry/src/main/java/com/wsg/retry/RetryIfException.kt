package com.wsg.retry

import java.lang.Exception

/**
 * 增加异常重试类型
 */
abstract class RetryIfException<T : Exception, E : Any> {
    /**
     * 重试条件
     * e为方法的返回值
     * true 进行重试
     */
    abstract fun <T, E> onRetryException(t: T?, result: E?): Boolean
}