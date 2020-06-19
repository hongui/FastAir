package com.mob.lee.fastair.fragment

import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.p2p.P2PManager
import com.mob.lee.fastair.utils.dialog
import com.mob.lee.fastair.utils.successToast
import com.mob.lee.fastair.viewmodel.DeviceViewModel
import kotlinx.android.synthetic.main.fragment_discover.*

/**
 * Created by Andy on 2017/8/11.
 */
class DiscoverFragment : AppFragment() {
    var stopDiscover = false
    override val layout: Int = R.layout.fragment_discover

    val viewModel by lazy {
        viewModel<DeviceViewModel>()
    }

    override fun setting() {
        setHasOptionsMenu(true)
        title(R.string.discoverDevice, false)

        P2PManager.devicesLiveData.observe({ lifecycle }) {
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
                    P2PManager.connect(mParent!!, device)
                    viewModel.saveDevice(context, device)
                }
                discoverView.addView(view)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_discover, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.menu_discover_help == item.itemId) {
            mParent?.dialog {
                setMessage(R.string.discover_help)
                        .setPositiveButton(R.string.knowIt, null)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}