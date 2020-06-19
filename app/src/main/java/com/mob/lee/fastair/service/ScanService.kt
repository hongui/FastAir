package com.mob.lee.fastair.service

import android.app.Service
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.os.IBinder
import androidx.lifecycle.Observer
import com.mob.lee.fastair.p2p.P2PManager
import com.mob.lee.fastair.viewmodel.DeviceViewModel

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/19 10:44
 * @Description:    无
 */
class ScanService : Service() {
    private val viewModel by lazy { DeviceViewModel() }
    private val observer by lazy {
        Observer<List<WifiP2pDevice>> { devices ->
            if (true == P2PManager.connectLiveData.value) {
                return@Observer
            }
            viewModel.readDevice(this) { device ->
                devices.find { it.deviceAddress == device }?.let {
                    P2PManager.connect(this, it)
                }
            }
        }
    }
    private val connectObserver by lazy {
        Observer<Boolean?> {
            if (true == it) {
                P2PManager.stopDiscovery(this)
            } else {
                P2PManager.discover(this)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        P2PManager.devicesLiveData.observeForever(observer)
        P2PManager.connectLiveData.observeForever(connectObserver)
        P2PManager.register(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        P2PManager.discover(this)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        P2PManager.devicesLiveData.removeObserver(observer)
        P2PManager.connectLiveData.removeObserver(connectObserver)
        P2PManager.unregister(this)
        P2PManager.stopDiscovery(this)
        P2PManager.stopConnect(this)
    }
}