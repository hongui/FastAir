package com.mob.lee.fastair.fragment

import com.mob.lee.fastair.base.AppFragment

import com.mob.lee.fastair.R

class SettingFragment : AppFragment() {
    override val layout: Int = R.layout.fragment_setting

    override fun setting() {
        title(R.string.settings)
    }
}