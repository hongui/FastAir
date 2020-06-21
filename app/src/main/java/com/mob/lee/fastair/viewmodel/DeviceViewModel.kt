package com.mob.lee.fastair.viewmodel

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import com.mob.lee.fastair.repository.SharedPreferenceDataSource
import java.net.InetAddress

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/19 11:08
 * @Description:    无
 */
class DeviceViewModel : AppViewModel() {
    private val name = "devices"
    private val device = "device"
    private val host = "host"
    private val groupOwner = "groupOwner"

    val dataSource by lazy {
        SharedPreferenceDataSource()
    }

    fun saveDevice(context: Context?, device: WifiP2pDevice?) {
        context ?: return
        device ?: return
        dataSource.writePreference(context, name) {
            putString(this@DeviceViewModel.device, device.deviceAddress)
        }
    }

    fun readDevice(context: Context?, action: (String?) -> Unit) {
        dataSource.readPreference(context, name) {
            action(getString(device, null))
        }
    }

    fun saveInfo(context: Context?, info: WifiP2pInfo?) {
        context ?: return
        info ?: return
        dataSource.writePreference(context, name) {
            putString(host, info.groupOwnerAddress?.hostAddress)
            putBoolean(groupOwner, info.isGroupOwner)
        }
    }

    fun readInfo(context: Context?, action: (String?,Boolean) -> Unit) {
        dataSource.readPreference(context, name) {
            val h = getString(host, null)
            val owner = getBoolean(groupOwner, false)
            action(h,owner)
        }
    }
}