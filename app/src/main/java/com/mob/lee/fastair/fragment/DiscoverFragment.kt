package com.mob.lee.fastair.fragment

import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.base.OnBackpressEvent
import com.mob.lee.fastair.p2p.P2PManager
import com.mob.lee.fastair.utils.dialog
import com.mob.lee.fastair.utils.successToast
import kotlinx.android.synthetic.main.fragment_discover.*

/**
 * Created by Andy on 2017/8/11.
 */
class DiscoverFragment : AppFragment(), OnBackpressEvent {
    var stopDiscover = false
    var backIt = false

    override fun layout() : Int = R.layout.fragment_discover

    override fun setting() {
        setHasOptionsMenu(true)
        toolbar(R.string.discoverDevice, false)

        P2PManager.devices.observe({ lifecycle }) {
            if (null == discoverView || null == it) {
                return@observe
            }
            if (stopDiscover) {
                return@observe
            }
            discoverView.removeAllViews()
            for (device in it) {
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
    }

    override fun onCreateOptionsMenu(menu : Menu?, inflater : MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_discover, menu)
    }

    override fun onOptionsItemSelected(item : MenuItem?) : Boolean {
        if (R.id.menu_discover_help == item?.itemId) {
            mParent?.dialog {
                it.setMessage(R.string.discover_help)
                        .setPositiveButton(R.string.knowIt, null)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        P2PManager.discover(context !!)
    }

    override fun onDestroy() {
        super.onDestroy()
        P2PManager.stopDiscovery(context !!)
    }

    override fun onPressed() : Boolean {
        if (! backIt) {
            mParent?.dialog {
                it.setMessage(R.string.disconverBackInfo)
                        .setPositiveButton(R.string.stop){
                            dialog, which ->
                            backIt = true
                            P2PManager.stopDiscovery(context !!)
                            mParent?.onBackPressed()
                        }
                        .setNegativeButton(R.string.justkid,null)
            }
            return true
        }
        return false
    }
}