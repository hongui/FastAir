package com.mob.lee.fastair.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.*

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/19 11:24
 * @Description:    无
 */
open class AppService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mScope.cancel()
        mScope.cancelChildren()
    }

    val mScope by lazy {
        SupervisorJob() + Dispatchers.Main
    }
}