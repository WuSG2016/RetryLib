package com.wsg.retry

import java.lang.Exception
import kotlin.reflect.KFunction


data class RetryBean<T>(
    val t: T,
    var kFunction: KFunction<*>,
    val errorMutableList: MutableList<Exception> = mutableListOf()
)



