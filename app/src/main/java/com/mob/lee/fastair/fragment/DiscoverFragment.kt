package com.mob.lee.fastair.fragment

import android.Manifest
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.p2p.failedReason
import com.mob.lee.fastair.utils.dialog
import com.mob.lee.fastair.utils.errorToast
import com.mob.lee.fastair.utils.successToast
import com.mob.lee.fastair.view.DiscoverView
import com.mob.lee.fastair.viewmodel.DeviceViewModel

/**
 * Created by Andy on 2017/8/11.
 */
class DiscoverFragment : AppFragment() {
    override val layout: Int = R.layout.fragment_discover

    val viewModel by lazy {
        activityViewModel<DeviceViewModel>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.registerPermission(this)
    }

    override fun setting() {
        setHasOptionsMenu(true)
        title(R.string.discover_device, true)

        val discoverView = view<DiscoverView>(R.id.discoverView)
        val deviceStatus = view<TextView>(R.id.device_status)
        viewModel.devices.observe {
            discoverView?.removeAllViews()
            for (device in it) {
                val view = LayoutInflater.from(context).inflate(R.layout.item_scan, null)
                val name = view?.findViewById<TextView>(R.id.item_scan_name)
                name?.text = device.deviceName
                view?.setOnClickListener {

                    viewModel.connect(device, object : WifiP2pManager.ActionListener {
                        override fun onSuccess() {
                            requireContext().successToast(R.string.connect_success)
                        }

                        override fun onFailure(reason: Int) {
                            requireContext().errorToast(failedReason(reason))
                        }

                    })
                }
                discoverView?.addView(view)
            }
        }
        viewModel.connectState.observe {
            if (true == it) {
                viewModel.stopDiscover()
                jump()
            }
        }
        viewModel.device.observe {
            val status = when (it?.status) {
                WifiP2pDevice.AVAILABLE -> R.string.device_status_available
                WifiP2pDevice.CONNECTED -> R.string.device_status_connected
                WifiP2pDevice.FAILED -> R.string.device_status_failed
                WifiP2pDevice.INVITED -> R.string.device_status_invaited
                else -> R.string.device_status_unavailable
            }
            deviceStatus?.text =
                getString(R.string.current_device_info, it?.deviceName, getString(status))
        }
        viewModel.withPermission(
            this,
            if (Build.VERSION.SDK_INT >= 33) {
                Manifest.permission.NEARBY_WIFI_DEVICES
            } else {
                Manifest.permission.ACCESS_FINE_LOCATION
            },
            action = { hasPermission ->
                if (hasPermission) {
                    viewModel.init(requireActivity())
                    viewModel.discover(
                        object : WifiP2pManager.ActionListener {
                            override fun onSuccess() {

                            }

                            override fun onFailure(reason: Int) {
                                context?.errorToast(failedReason(reason))
                            }
                        })
                } else {
                    mParent?.dialog {
                        setMessage(R.string.need_location)
                            .setPositiveButton(R.string.turn_on) { _, _ ->
                                viewModel.openSetting(this@DiscoverFragment)
                            }
                            .setNegativeButton(R.string.cancel) { _, _ ->
                                requireContext().errorToast(R.string.rejected_location)
                            }
                    }
                }
            })
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

    fun jump() {
        val needToTarget = arguments?.containsKey("target") ?: false
        if (needToTarget) {
            val target = requireArguments().getInt("target")
            navigation(target, args = { putAll(arguments) }, options = {
                setPopUpTo(R.id.discoverFragment, true)
            })
        } else {
            mParent?.onBackPressed()
        }
    }
}