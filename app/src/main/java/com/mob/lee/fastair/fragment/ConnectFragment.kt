package com.mob.lee.fastair.fragment

import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.viewmodel.DeviceViewModel

abstract class ConnectFragment : AppFragment() {

    override fun setting() {
        if (!activityViewModel<DeviceViewModel>().isConnected()) {
            navigation(R.id.discoverFragment)
        } else {
            super.setting()
        }
    }
}