package com.mob.lee.fastair.p2p

import android.net.wifi.p2p.WifiP2pManager
import com.mob.lee.fastair.R

fun failedReason(reason: Int):Int{
    return when(reason){
        WifiP2pManager.P2P_UNSUPPORTED -> R.string.p2p_unsupport_error
        WifiP2pManager.BUSY ->R.string.p2p_busy_error
        else->R.string.p2p_inner_error
    }
}