package com.wsg.retry

/**
 * 网络监听
 */
interface INetworkListener {
    /**
     * 网络状态
     */
    fun onNetworkState(state: Boolean)
}