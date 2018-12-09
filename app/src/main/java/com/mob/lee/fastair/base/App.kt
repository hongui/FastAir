package com.mob.lee.fastair.base

import android.app.Application
import com.mob.lee.fastair.p2p.P2PManager

/**
 * Created by Andy on 2017/6/1.
 */

class App : Application(){
    override fun onCreate() {
        super.onCreate()
        P2PManager.register(this)
    }
}