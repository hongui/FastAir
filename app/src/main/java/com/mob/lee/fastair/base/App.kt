package com.mob.lee.fastair.base

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

/**
 * Created by Andy on 2017/6/1.
 */

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
    }
}