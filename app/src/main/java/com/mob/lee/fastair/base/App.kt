package com.mob.lee.fastair.base

import android.content.Intent
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.mob.lee.fastair.service.ScanService

/**
 * Created by Andy on 2017/6/1.
 */

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)

        val intent = Intent(this, ScanService::class.java)
        startService(intent)
    }
}