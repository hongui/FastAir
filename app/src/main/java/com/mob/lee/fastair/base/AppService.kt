package com.mob.lee.fastair.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/19 11:24
 * @Description:    无
 */
open class AppService : Service(), CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main
    private val mViewModelStore by lazy {
        ViewModelStore()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }
}