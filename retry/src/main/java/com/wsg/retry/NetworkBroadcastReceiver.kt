package com.wsg.retry

import android.annotation.SuppressLint

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.lang.Exception

/**
 * 网络监听
 */
class NetworkBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val MOBILE = 1001
        private const val NETWORK_WIFI = 1002
        private const val ETHERNET = 1003
        const val NETWORK_NONE = -1
        var listener: INetworkListener? = null
        const val NETWORK_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
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
                } else if (activeNetworkInfo.type == (ConnectivityManager.TYPE_ETHERNET)) {
                    return ETHERNET
                }
            }
            return NETWORK_NONE
        }
    }

    /**
     * 网络是否连接
     */

    private fun checkNet(context: Context): Boolean {
        try {
            val connectivity:ConnectivityManager = context.getSystemService (Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connectivity.activeNetworkInfo
            if (info != null && info.isConnected) {
                if (info.state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val netWorkState = checkNet(context!!)
            listener?.onNetworkState(netWorkState)
        }

    }


}