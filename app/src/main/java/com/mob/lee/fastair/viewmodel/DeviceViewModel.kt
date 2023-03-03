package com.mob.lee.fastair.viewmodel

import android.Manifest
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/19 11:08
 * @Description:    无
 */
class DeviceViewModel : AppViewModel() {
    val state by lazy { MutableLiveData<Boolean>() }
    val connectState by lazy { MutableLiveData<Boolean>() }
    val action by lazy { MutableLiveData<Pair<Int, Int>>() }
    val p2pInfo by lazy { MutableLiveData<WifiP2pInfo?>() }
    val device by lazy { MutableLiveData<WifiP2pDevice?>() }
    val devices by lazy { MutableLiveData<Collection<WifiP2pDevice>>() }

    internal var manager: WifiP2pManager? = null

    private val receiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.e("Tag", "-----${intent?.action}------------")
                when (intent?.action) {
                    WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> state.value = WifiP2pManager.WIFI_P2P_STATE_ENABLED == intent.getIntExtra(
                        WifiP2pManager.EXTRA_WIFI_STATE, WifiP2pManager.WIFI_P2P_STATE_DISABLED
                    )

                    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> manager?.requestPeers(channel) {
                        devices.value = it.deviceList
                    }


                    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                        p2pInfo.value = intent.parcelableExtra<WifiP2pInfo>(WifiP2pManager.EXTRA_WIFI_P2P_INFO)
                        connectState.value=true == p2pInfo.value?.groupFormed
                    }

                    WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> device.value = intent.parcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                }
            }
        }
    }

    private var channel: WifiP2pManager.Channel? = null
    private var connectTimes= RETRY_TIMES

    fun init(activity: FragmentActivity) {
        if (hasPermission(activity)) {
            manager = ContextCompat.getSystemService(activity, WifiP2pManager::class.java)

            val c = channel ?: let {
                registerReceiver(activity)

                manager?.initialize(activity, activity.mainLooper) {
                    stopScan()
                }?.apply { channel = this }
            }
            if (Build.VERSION.SDK_INT >= 29 && null != c) {
                requestInfo(c)
            }
        }
    }

    fun discover(listener: WifiP2pManager.ActionListener?) {
        connectTimes= RETRY_TIMES
        channel?.let {
            manager?.discoverPeers(it, object :WifiP2pManager.ActionListener{
                override fun onSuccess() {
                    connectTimes= RETRY_TIMES
                }

                override fun onFailure(reason: Int) {
                    if(WifiP2pManager.BUSY==reason){
                        if (0 != connectTimes) {
                            connectTimes-=1
                            disconnect(null)
                            manager?.discoverPeers(it,this)
                        }
                    }
                    listener?.onFailure(reason)
                }
            })
        }
    }

    fun connect(device: WifiP2pDevice, listener: WifiP2pManager.ActionListener?) {
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress

        manager?.connect(channel, config, listener)
    }

    fun disconnect(actionListener: WifiP2pManager.ActionListener?) {
        manager?.removeGroup(channel, actionListener)
        manager?.cancelConnect(channel, actionListener)
        connectState.value = false
        p2pInfo.value = null
    }

    fun withConnectNavigation(
        fragment: AppFragment, target: Int, args: (Bundle.() -> Unit)? = null
    ) {
        if (isConnected()) {
            fragment.navigation(target, args)
        } else {
            fragment.navigation(R.id.discoverFragment, args)
        }
    }

    fun isConnected() = connectState.value == true

    fun bundle(): Bundle {
        val info = p2pInfo.value
        val bundle = Bundle()
        bundle.putString(HOST, info?.groupOwnerAddress?.hostAddress)
        bundle.putBoolean(GROUP_OWNER, info?.isGroupOwner ?: false)
        return bundle
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun requestInfo(channel: WifiP2pManager.Channel) {
        manager?.apply {
            requestConnectionInfo(channel) {
                p2pInfo.value = it
            }
            requestDeviceInfo(channel) {
                device.value = it
            }
        }

    }

    private fun hasPermission(context: Context): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= 33) {
            Manifest.permission.NEARBY_WIFI_DEVICES
        } else {
            Manifest.permission.ACCESS_FINE_LOCATION
        }
        return ContextCompat.checkSelfPermission(
            context, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun registerReceiver(context: FragmentActivity) {
        context.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                context.unregisterReceiver(receiver)
            }
        })

        val intentFilter = IntentFilter()
        /*指示Wi-Fi P2P是否开启*/
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        /*对等节点列表发生了变化*/
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        /*连接状态发生了变化*/
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        /*设备配置发生了变化*/
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        context.registerReceiver(receiver, intentFilter)

    }

    private fun stopScan() {
        channel?.let {
            disconnect(null)
            manager?.stopPeerDiscovery(it, null)
            channel = null
        }
    }

    private inline fun <reified T> Intent?.parcelableExtra(name: String) = if (Build.VERSION.SDK_INT >= 33) {
        this?.getParcelableExtra(
            name, T::class.java
        )
    } else {
        this?.getParcelableExtra(name)
    }

    companion object {
        @JvmStatic
        val HOST = "host"

        @JvmStatic
        val GROUP_OWNER = "groupOwner"

        @JvmStatic
        val RETRY_TIMES = 3

        fun unBundle(bundle: Bundle?): Pair<String?, Boolean> {
            bundle ?: return null to false
            return bundle.getString(HOST, null) to bundle.getBoolean(GROUP_OWNER, false)
        }
    }
}