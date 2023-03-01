package com.mob.lee.fastair.p2p

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.util.Log

/**
 * Created by Andy on 2017/8/29.
 */
class P2PReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("TAG", intent?.action.toString())
        when (intent?.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                WifiP2pManager.EXTRA_WIFI_P2P_INFO
                P2PManager.enableLiveData.value =
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED == intent.getIntExtra(
                        WifiP2pManager.EXTRA_WIFI_STATE,
                        WifiP2pManager.WIFI_P2P_STATE_DISABLED
                    )
            }

            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                P2PManager.manager?.requestPeers(P2PManager.channel) {
                    val deviceList = it.deviceList
                    if (deviceList.isEmpty()) {
                        return@requestPeers
                    }
                    val devices = ArrayList<WifiP2pDevice>()
                    for (d in deviceList) {
                        if (null != d) {
                            devices.add(d)
                        }
                    }
                    P2PManager.devicesLiveData.value = devices
                }
            }

            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val wifiP2pInfo = if (Build.VERSION.SDK_INT >= 33) {
                    intent.getParcelableExtra(
                        WifiP2pManager.EXTRA_WIFI_P2P_DEVICE,
                        WifiP2pInfo::class.java
                    )
                } else {
                    intent.getParcelableExtra<WifiP2pInfo>(WifiP2pManager.EXTRA_WIFI_P2P_INFO)
                }
                if (true == wifiP2pInfo?.groupFormed) {
                    P2PManager.p2pInfoLiveData.value = wifiP2pInfo
                    P2PManager.connectLiveData.value = true
                } else if (true == P2PManager.connectLiveData.value) {
                    P2PManager.p2pInfoLiveData.value = null
                    P2PManager.connectLiveData.value = false
                }
            }

            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val device = if (Build.VERSION.SDK_INT >= 33) {
                    intent.getParcelableExtra(
                        WifiP2pManager.EXTRA_WIFI_P2P_DEVICE,
                        WifiP2pDevice::class.java
                    )
                } else {
                    intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                }
                P2PManager.currentDevice.value = device
            }
        }
    }
}