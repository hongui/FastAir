package com.mob.lee.fastair.p2p

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import com.mob.lee.fastair.model.ADDRESS
import com.mob.lee.fastair.model.IS_HOST

/**
 * Created by Andy on 2017/8/16.
 */
object P2PManager {
    val devices = ArrayList<WifiP2pDevice>()
    val subcriper = ArrayList<Subscriber>()
    var receiver: P2PReceiver? = null
    var manager: WifiP2pManager? = null
    var channel: WifiP2pManager.Channel? = null
    var p2pInfo:WifiP2pInfo?=null
    var connected=false


    fun register(context: Context) {
        val intentFilter = IntentFilter()
        /*指示Wi-Fi P2P是否开启*/
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        /*对等节点列表发生了变化*/
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        /*连接状态发生了变化*/
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        /*设备配置发生了变化*/
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        receiver = P2PReceiver()
        context.registerReceiver(receiver, intentFilter)

        manager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager!!.initialize(context, context.mainLooper, object : WifiP2pManager.ChannelListener {
            override fun onChannelDisconnected() {
                unregister(context)
            }
        })
    }

    fun unregister(context : Context){
        connected(false)
        p2pInfo=null
        removeService(context)
        disconnect(context)
    }

    fun bundle():Bundle{
        val bundle = Bundle()
        bundle.putString(ADDRESS, p2pInfo?.groupOwnerAddress?.hostAddress)
        bundle.putBoolean(IS_HOST, p2pInfo?.isGroupOwner?:false)
        return bundle
    }

    fun unBundle(bundle : Bundle?):Pair<String?,Boolean>{
        bundle?:return null to false
        return bundle.getString(ADDRESS,null) to bundle.getBoolean(IS_HOST,false)
    }

    fun discover(context : Context){
        manager!!.discoverPeers(channel, ActionListener(context))
    }

    fun connect(context: Context, device: WifiP2pDevice) {
        val wps = WpsInfo()
        wps.setup = WpsInfo.PBC

        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        config.wps = wps
        manager?.connect(channel, config, ActionListener(context))
    }

    fun connectInfo(info: (info: WifiP2pInfo) -> Unit) {
        manager?.requestConnectionInfo(channel, info)
    }

    fun stop(context: Context) {
        if (null != channel) {
            manager?.stopPeerDiscovery(channel, ActionListener(context))
        }
    }

    fun removeService(context: Context) {
        if (null != receiver) {
            context.unregisterReceiver(receiver)
        }
    }

    fun add(subscriber: Subscriber) {
        synchronized(this) {
            subcriper.add(subscriber)
        }
    }

    fun remove(subscriber: Subscriber) {
        synchronized(this) {
            subcriper.remove(subscriber)
        }
    }

    fun enable(enable: Boolean) {
        for (subcriber in subcriper) {
            subcriber.wifiState(enable)
        }
    }

    fun peer(list:List<WifiP2pDevice>) {
        for (device in list) {
            if (devices.contains(device)) {
                continue
            }
            devices.add(device)
        }
        for (subcriber in subcriper) {
            subcriber.peers(devices)
        }
    }

    fun connected(connected: Boolean,info:WifiP2pInfo?=null) {
        for (subcriber in subcriper) {
            subcriber.connect(connected)
        }
        this.connected=connected
        this.p2pInfo=info
    }

    fun disconnect(context: Context){
        stop(context)
        manager?.removeGroup(channel,ActionListener(context))
    }
}