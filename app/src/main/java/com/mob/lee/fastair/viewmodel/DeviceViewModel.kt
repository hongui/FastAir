package com.mob.lee.fastair.viewmodel

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import com.mob.lee.fastair.repository.SharedPreferenceDataSource

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/19 11:08
 * @Description:    无
 */
class DeviceViewModel : AppViewModel() {
    private val name = "devices"
    private val key = "device"

    val dataSource by lazy {
        SharedPreferenceDataSource()
    }

    fun saveDevice(context: Context?, device: WifiP2pDevice?) {
        context ?: return
        device ?: return
        dataSource.writePreference(context, name) {
            putString(key, device.deviceAddress)
        }
    }

    fun readDevice(context: Context?, action: (String?) -> Unit) {
        dataSource.readPreference(context, name) {
            action(getString(key, null))
        }
    }
}