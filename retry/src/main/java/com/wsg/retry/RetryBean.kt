package com.wsg.retry

import kotlin.reflect.KFunction


data class RetryBean<T>(
    val t: T,
    var kFunction: KFunction<*>,
    var retryCount: Int = 0
)



