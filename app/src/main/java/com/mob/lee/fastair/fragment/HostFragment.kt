package com.mob.lee.fastair.fragment

import android.content.Context
import android.net.wifi.WifiManager
import androidx.lifecycle.coroutineScope
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import kotlinx.android.synthetic.main.fragment_host.*

class HostFragment() :AppFragment() {
    override val layout: Int= R.layout.fragment_host

    override fun setting() {
        Host.host {
            val info=start(lifecycle.coroutineScope)
            tv_host.text=info
        }
        val manager=mParent?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connect=manager.connectionInfo
        val ip=connect.ipAddress
        tv_host.text="${ip and 0xff}.${(ip shr 8) and 0xff}.${(ip shr 16) and 0xff}.${(ip shr 24) and 0xff}"
    }
}