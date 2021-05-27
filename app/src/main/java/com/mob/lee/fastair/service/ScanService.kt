package com.mob.lee.fastair.service

import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.os.IBinder
import androidx.lifecycle.Observer
import com.mob.lee.fastair.base.AppService
import com.mob.lee.fastair.p2p.P2PManager
import com.mob.lee.fastair.viewmodel.DeviceViewModel

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/19 10:44
 * @Description:    无
 */
class ScanService : AppService() {
    private val viewModel by lazy { viewModel<DeviceViewModel>() }

    private val observer by lazy {
        Observer<List<WifiP2pDevice?>> { devices ->
            devices ?: return@Observer
            if (P2PManager.isConnected()) {
                return@Observer
            }
            viewModel.readDevice(this) { device ->
                devices.find { it?.deviceAddress == device }?.let {
                    if (!P2PManager.isConnected()) {
                        P2PManager.connect(this, it)
                    }
                }
            }
        }
    }

    private val connectObserver by lazy {
        Observer<Boolean?> {
            val intent = Intent(this, FileService::class.java)
            if (true == it) {
                P2PManager.stopDiscovery(this)
                startService(intent)
            } else {
                P2PManager.discover(this)
                stopService(intent)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        //自动重连，比较鸡肋，先去掉了
        //P2PManager.devicesLiveData.observeForever(observer)
        P2PManager.connectLiveData.observeForever(connectObserver)
        P2PManager.register(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY_COMPATIBILITY
    }

    override fun onDestroy() {
        super.onDestroy()
        P2PManager.devicesLiveData.removeObserver(observer)
        P2PManager.connectLiveData.removeObserver(connectObserver)
        P2PManager.stopDiscovery(this)
        P2PManager.unregister(this)
    }

    companion object {
        fun startScan(context: Context?) {
            val intent = Intent(context, ScanService::class.java)
            context?.stopService(intent)
            context?.startService(intent)
        }
    }
}