package com.mob.lee.fastair.p2p

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import com.mob.lee.fastair.utils.errorToast

/**
 * Created by Andy on 2017/8/17.
 */
class ActionListener(val context: Context?) : WifiP2pManager.ActionListener {
    override fun onSuccess() {

    }

    override fun onFailure(reason: Int) {
        when (reason) {

            WifiP2pManager.P2P_UNSUPPORTED -> context?.errorToast("该设备不支持Wi-Fi P2P服务")

            WifiP2pManager.BUSY -> context?.errorToast("当前设备服务忙")

            WifiP2pManager.ERROR -> context?.errorToast("服务内部错误")
        }
    }
}