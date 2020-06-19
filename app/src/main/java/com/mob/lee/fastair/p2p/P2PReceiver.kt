package com.mob.lee.fastair.p2p

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager

/**
 * Created by Andy on 2017/8/29.
 */
class P2PReceiver : BroadcastReceiver() {
    override fun onReceive(context : Context?, intent : Intent?) {
        when (intent?.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                WifiP2pManager.EXTRA_WIFI_P2P_INFO
                P2PManager.enableLiveData.value = WifiP2pManager.WIFI_P2P_STATE_ENABLED == intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, WifiP2pManager.WIFI_P2P_STATE_DISABLED)
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
                val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                val wifiP2pInfo = intent.getParcelableExtra<WifiP2pInfo>(WifiP2pManager.EXTRA_WIFI_P2P_INFO)
                val connected = P2PManager.isConnected()
                if (connected && ! networkInfo.isConnected) {
                    P2PManager.connectLiveData.value = false
                    P2PManager.p2pInfoLiveData.value = null
                } else if (networkInfo.isConnected) {
                    P2PManager.connectLiveData.value = true
                    P2PManager.p2pInfoLiveData.value = wifiP2pInfo
                }
            }
        }
    }
}