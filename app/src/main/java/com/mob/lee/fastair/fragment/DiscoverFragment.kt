package com.mob.lee.fastair.fragment

import kotlinx.coroutines.launch
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.TextView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.base.OnBackpressEvent
import com.mob.lee.fastair.model.*
import com.mob.lee.fastair.p2p.P2PManager
import com.mob.lee.fastair.p2p.Subscriber
import com.mob.lee.fastair.utils.database
import com.mob.lee.fastair.utils.successToast
import kotlinx.android.synthetic.main.fragment_discover.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Created by Andy on 2017/8/11.
 */
class DiscoverFragment : AppFragment(), Subscriber, OnBackpressEvent {
    var stopDiscover = false
    var backIt = false

    override fun wifiState(enable : Boolean) {
        if (enable) {
            return
        }
        showDialog(getString(R.string.disconverStateTips), { dialog, which ->
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            context?.startActivity(intent)
        }, getString(R.string.goTurnOn))
    }

    override fun peers(devices : List<WifiP2pDevice>) {
        if (null == discoverView) {
            return
        }
        if (stopDiscover) {
            return
        }
        discoverView.removeAllViews()
        for (device in devices) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_scan, null)
            val name = view?.findViewById<TextView>(R.id.item_scan_name)
            name?.text = device.deviceName
            view?.setOnClickListener {
                stopDiscover = true
                context?.successToast("正在建立连接，请稍后...")
                P2PManager.connect(context !!, device)
            }
            discoverView.addView(view)
        }
    }

    override fun connect(connected : Boolean) {
        if (connected) {
            if (arguments?.getBoolean("isJustConnect") ?: false) {
                context?.successToast("连接成功,可以收发信息了")
                backIt = true
                mParent?.onBackPressed()
                return
            }
            context?.successToast("连接成功,正在跳转，请稍等...")
            P2PManager.connectInfo({ info ->
                P2PManager.stop(mParent !!)
                val isChat = arguments?.getBoolean(IS_CHAT, false) ?: false
                val bundle = Bundle()
                bundle.putString(ADDRESS, info.groupOwnerAddress.hostAddress)
                bundle.putBoolean(IS_HOST, info.isGroupOwner)
                bundle.putAll(arguments)
                if (isChat) {
                    mParent?.fragment(ChatFragment::class, bundle, addToIt = false)
                } else {
                    mParent?.fragment(HistoryFragment::class, bundle, addToIt = false)
                }
                mParent?.database(mScope, { dao ->
                    val records = dao.checkedRecords()
                    records?.let {
                        it.forEach {
                            it.state = STATE_WAIT
                        }
                        dao.updates(it)
                    }
                })
            })
        }
    }

    override fun layout() : Int = R.layout.fragment_discover

    override fun setting() {
        toolbar(R.string.discoverDevice, true)

        P2PManager.add(this)
    }

    override fun onResume() {
        super.onResume()

        P2PManager.discover(context !!)
    }

    override fun onDestroy() {
        super.onDestroy()
        P2PManager.stop(context !!)
        P2PManager.remove(this)
    }

    override fun onPressed() : Boolean {
        if (! backIt) {
            showDialog(getString(R.string.disconverBackInfo), { dialog, which ->
                backIt = true
                P2PManager.stop(context !!)
                P2PManager.remove(this)
                mParent?.onBackPressed()
            }, getString(R.string.stop))
            return true
        }
        return false
    }
}