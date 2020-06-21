package com.mob.lee.fastair.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.mob.lee.fastair.viewmodel.AppViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/19 11:24
 * @Description:    无
 */
open class AppService : Service(), CoroutineScope, ViewModelStoreOwner {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate
    private val mViewModelStore by lazy {
        ViewModelStore()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModelStore.clear()
        coroutineContext.cancel()
        coroutineContext.cancelChildren()
    }

    override fun getViewModelStore(): ViewModelStore = mViewModelStore

    inline fun <reified D : AppViewModel> viewModel(): D {
        val viewModel = ViewModelProvider(this).get(D::class.java)
        return viewModel
    }
}