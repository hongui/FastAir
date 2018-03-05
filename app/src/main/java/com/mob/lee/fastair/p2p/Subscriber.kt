package com.mob.lee.fastair.p2p

import android.net.wifi.p2p.WifiP2pDevice

/**
 * Created by Andy on 2017/8/17.
 */
interface Subscriber{
    fun wifiState(enable:Boolean)

    fun peers(devices:List<WifiP2pDevice>)

    fun connect(connected:Boolean)
}