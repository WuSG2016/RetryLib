package com.retail.newretail.vmc

import kotlin.reflect.KFunction


data class RetryBean<T>(val t: T, var kFunction: KFunction<*>)



