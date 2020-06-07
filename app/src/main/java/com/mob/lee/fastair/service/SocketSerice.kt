package com.mob.lee.fastair.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.mob.lee.fastair.io.SocketService
import com.mob.lee.fastair.io.StringReader
import com.mob.lee.fastair.io.state.State
import com.mob.lee.fastair.model.ADDRESS
import com.mob.lee.fastair.model.IS_HOST
import com.mob.lee.fastair.model.PORT_MESSAGE
import kotlinx.coroutines.async

abstract class SocketSerice : Service() {
    var mSocket: SocketService? = null
    var mHandler: Handler? = null
    val mScope by lazy {
        ServiceScope.of()
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
        return BinderImpl(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (null == intent && null != mSocket) {
            return super.onStartCommand(intent, flags, startId)
        }
        val host = intent?.getStringExtra(ADDRESS) ?: ""
        val isHost = intent?.getBooleanExtra(IS_HOST, false) ?: false
        content(host, isHost,intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mSocket?.close()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mScope.close()
    }

    open fun content(host: String, isHost: Boolean,intent: Intent?) {
        mSocket = SocketService( true)
        mScope.async {
            mSocket?.open(PORT_MESSAGE, if (isHost) null else host)
            mSocket?.let {
                connected(it)
            }
            onNewTask(intent)
        }
    }

    abstract fun onReceiveMessage(message: State)

    abstract suspend fun onNewTask(intent: Intent?)

    abstract suspend fun connected(socket:SocketService)
}