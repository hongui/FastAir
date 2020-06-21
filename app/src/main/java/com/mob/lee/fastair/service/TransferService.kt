package com.mob.lee.fastair.service

import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.mob.lee.fastair.base.AppService
import com.mob.lee.fastair.io.SocketService
import com.mob.lee.fastair.io.state.State
import com.mob.lee.fastair.model.PORT_MESSAGE
import com.mob.lee.fastair.viewmodel.DeviceViewModel
import kotlinx.coroutines.async

abstract class TransferService : AppService() {
    var mSocket: SocketService? = null
    var mHandler: Handler? = null

    override fun onCreate() {
        mHandler = Handler {
            val data = it.obj as? State
            data ?: return@Handler false
            onReceiveMessage(data)
            true
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return BinderImpl(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (null == intent && null != mSocket) {
            return super.onStartCommand(intent, flags, startId)
        }
        viewModel<DeviceViewModel>().readInfo(this) { host, groupOwner ->
            content(host, groupOwner, intent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mSocket?.close()
        return super.onUnbind(intent)
    }

    open fun content(host: String?, isHost: Boolean, intent: Intent?) {
        mSocket = SocketService(true)
        async {
            mSocket?.open(PORT_MESSAGE, if (isHost) null else host)
            mSocket?.let {
                connected(it)
            }
            onNewTask(intent)
        }
    }

    abstract fun onReceiveMessage(message: State)

    abstract suspend fun onNewTask(intent: Intent?)

    abstract suspend fun connected(socket: SocketService)
}