package com.wsg.retry

import kotlin.reflect.KFunction


class RetryBean<T>(
    val t: T,
    var kFunction: KFunction<*>,
    var retryCount: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other is RetryBean<*>) {
            if (other.t == this.t && this.kFunction == other.kFunction) {
                return true
            }
        }

        return false
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + t.hashCode()
        result = 31 * result + kFunction.hashCode()
        return result
    }

    override fun toString(): String {
        return "t:" + t.toString() + "kFunction:" + kFunction.toString() + "retryCount:" + retryCount
    }
}



