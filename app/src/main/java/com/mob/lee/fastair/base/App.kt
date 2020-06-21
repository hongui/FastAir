package com.mob.lee.fastair.base

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

        ScanService.startScan(this)
    }
}