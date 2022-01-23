package com.mob.lee.fastair.fragment

import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.io.getNetIP
import com.mob.lee.fastair.service.HostService
import kotlinx.android.synthetic.main.fragment_host.*

class HostFragment() : AppFragment() {
    override val layout: Int = R.layout.fragment_host

    override fun setting() {
        val port=9527
        tv_host.text = "${getNetIP(mParent!!)}:${port}"
        HostService.start(requireContext(),port)
    }

    override fun onStop() {
        super.onStop()
    }
}