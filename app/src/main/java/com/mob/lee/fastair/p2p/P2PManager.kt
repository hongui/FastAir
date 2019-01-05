package com.mob.lee.fastair.p2p

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.mob.lee.fastair.model.ADDRESS
import com.mob.lee.fastair.model.IS_HOST

/**
 * Created by Andy on 2017/8/16.
 */
object P2PManager {
    //设备信息列表
    val devices = MutableLiveData<List<WifiP2pDevice>>()
    //连接状态
    val connected=MutableLiveData<Boolean?>()
    //使能状态
    val enable= MutableLiveData<Boolean>()
    //连接信息
    var p2pInfo=MutableLiveData<WifiP2pInfo?>()

    var receiver: P2PReceiver? = null
    var manager: WifiP2pManager? = null
    var channel: WifiP2pManager.Channel? = null

    init {
        connected.value=null
    }

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
        devices.value=null
        connected.value= null
        p2pInfo.value=null
        stopReceiver(context)
        stopConnect(context)
    }

    fun stopReceiver(context: Context) {
        if (null != receiver) {
            context.unregisterReceiver(receiver)
        }
    }

    fun stopConnect(context: Context){
        manager?.removeGroup(channel,null)
        manager?.cancelConnect(channel,null)
    }

    fun bundle():Bundle{
        val info= p2pInfo.value
        val bundle = Bundle()
        bundle.putString(ADDRESS, info?.groupOwnerAddress?.hostAddress)
        bundle.putBoolean(IS_HOST, info?.isGroupOwner?:false)
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

    fun stopDiscovery(context: Context) {
        if (null != channel) {
            devices.value=null
            manager?.stopPeerDiscovery(channel, ActionListener(context))
        }
    }

    fun isConnected()=true==P2PManager.connected.value
}