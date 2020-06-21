package com.mob.lee.fastair.fragment

import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.p2p.P2PManager

abstract class ConnectFragment : AppFragment() {

    override fun setting() {
        if (!P2PManager.isConnected()) {
            navigation(R.id.discoverFragment)
        } else {
            super.setting()
        }
    }
}