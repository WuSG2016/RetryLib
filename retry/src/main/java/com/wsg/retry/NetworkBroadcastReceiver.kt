package com.wsg.retry

import android.annotation.SuppressLint

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

/**
 * 网络监听
 */
class NetworkBroadcastReceiver : BroadcastReceiver() {


    companion object {
        const val MOBILE = 1001
        const val NETWORK_WIFI = 1002
        const val NETWORK_NONE = -1
        var listener: INetworkListener? = null
        const val NETWORK_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val netWorkState = getNetworkState(context)
            listener?.onNetworkState(netWorkState)
        }

    }

    /**
     * 获取网络状态
     */
    @SuppressLint("MissingPermission")
    fun getNetworkState(context: Context?): Int {
        val connectivityManager: ConnectivityManager =
            context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            if (activeNetworkInfo.type == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI//wifi
            } else if (activeNetworkInfo.type == (ConnectivityManager.TYPE_MOBILE)) {
                return MOBILE//mobile
            }
        } else {
            return NETWORK_NONE
        }
        return NETWORK_NONE
    }
}