package com.mob.lee.fastair.io

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.NetworkInterface

fun getNetIP(context: Context): String? {
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return manager.activeNetworkInfo?.run {
        if (!isAvailable || !isConnected) {
            null
        }
        when (type) {
            ConnectivityManager.TYPE_MOBILE -> localIP()
            ConnectivityManager.TYPE_WIFI -> wifiIP(context)
            else -> null
        }
    }
}

private fun wifiIP(context: Context): String? {
    val manager = context.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as? WifiManager
    return manager?.connectionInfo?.run { ipToString(ipAddress) }
}

private fun localIP(): String? {
    val networkInterfaces = NetworkInterface.getNetworkInterfaces()
    while (networkInterfaces.hasMoreElements()) {
        val net = networkInterfaces.nextElement()
        if(!net.name.contains("wlan")){
            continue
        }
        val address = net.inetAddresses
        while (address.hasMoreElements()) {
            val add = address.nextElement()
            if (add is Inet4Address) {
                return add.hostAddress
            }
        }
    }
    return null
}

private fun ipToString(value: Int) = "${value and 0xff}.${value shr 8 and 0xff}.${value shr 16 and 0xff}.${value shr 24 and 0xff}"
