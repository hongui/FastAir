package com.mob.lee.fastair.service

import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.mob.lee.fastair.base.AppService
import com.mob.lee.fastair.io.SocketService
import com.mob.lee.fastair.io.state.STATE_CONNECTED
import com.mob.lee.fastair.io.state.SocketStateListener
import com.mob.lee.fastair.io.state.State
import com.mob.lee.fastair.model.Args
import com.mob.lee.fastair.viewmodel.DeviceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class TransferService : AppService() {
    var mSocket: SocketService? = null
    var mHandler: Handler? = null
    open var host: String? = null
    open var port: Int? = null
    open var groupOwner: Boolean? = null

    companion object {
        const val DEFAULT_PORT = 9527
    }

    override fun onCreate() {
        mHandler = Handler {
            val data = it.obj as? State
            data ?: return@Handler false
            onReceiveMessage(data)
            true
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        if (null != mSocket) {
            launch { onNewTask(intent) }
        }
        return BinderImpl(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (null != mSocket) {
            launch { onNewTask(intent) }
            return super.onStartCommand(intent, flags, startId)
        }
        viewModel<DeviceViewModel>().readInfo(this) { h, isHost ->
            if (null == host) {
                host = h
            }
            if (null == port) {
                port = intent?.getIntExtra(Args.PORT, DEFAULT_PORT)
            }
            if (null == groupOwner) {
                groupOwner = isHost
            }
            connect(intent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket?.close()
    }

    open fun connect(intent: Intent?) {
        mSocket = SocketService(true)
        var listener: SocketStateListener? = null
        listener = mSocket?.addListener { state, _ ->
            if (STATE_CONNECTED == state) {
                mSocket?.removeListener(listener)
                launch (Dispatchers.IO) {
                    connected(mSocket!!)
                    onNewTask(intent)
                }
            }
        }
        launch(Dispatchers.IO) {
            mSocket?.open(this,port ?: DEFAULT_PORT, if (true == groupOwner) null else host)
        }
    }

    abstract fun onReceiveMessage(message: State)

    abstract suspend fun onNewTask(intent: Intent?)

    abstract suspend fun connected(socket: SocketService)
}