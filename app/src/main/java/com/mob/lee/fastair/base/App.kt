package com.mob.lee.fastair.base

import android.app.Application
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.mob.lee.fastair.p2p.P2PManager

/**
 * Created by Andy on 2017/6/1.
 */

class App : MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        P2PManager.register(this)
    }
}