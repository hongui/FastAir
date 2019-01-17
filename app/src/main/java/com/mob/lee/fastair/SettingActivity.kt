package com.mob.lee.fastair

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.mob.lee.fastair.base.AppActivity
import com.mob.lee.fastair.fragment.SettingFragment

class SettingActivity : AppActivity() {

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_setting)

        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.setting)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener { onBackPressed() }

        fragment(SettingFragment::class,content = R.id.setting_fragment)
    }
}