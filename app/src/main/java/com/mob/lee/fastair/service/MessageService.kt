package com.mob.lee.fastair.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.mob.lee.fastair.base.AndroidScope
import com.mob.lee.fastair.io.*
import com.mob.lee.fastair.io.state.State
import com.mob.lee.fastair.model.ADDRESS
import com.mob.lee.fastair.model.IS_HOST
import com.mob.lee.fastair.model.PORT_MESSAGE

/**
 * Created by Andy on 2017/8/29.
 */
class MessageService : Service() {
    var socket: SocketService? = null
    var mHandler: Handler? = null
    var mMessageListener: ProcessListener? = null

    val mScope: AndroidScope by lazy {
        AndroidScope()
    }

    override fun onCreate() {
        super.onCreate()
        mHandler = Handler {
            val data = it.obj as? State
            data ?: return@Handler false
            mMessageListener?.invoke(data)
            true
        }
        mScope.create()
    }

    override fun onBind(intent: Intent?): IBinder {
        return BinderImpl(this)
    }

    fun write(content: String) {
        socket?.write(StringWriter(content))
    }

    fun close() {
        socket?.close()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (null == intent && null != socket) {
            return super.onStartCommand(intent, flags, startId)
        }
        val host = intent?.getStringExtra(ADDRESS)
        val isHost = intent?.getBooleanExtra(IS_HOST, false) ?: false
        socket = SocketService(mScope, true)
        socket?.open(PORT_MESSAGE, if (isHost) null else host)
        socket?.read(StringReader() {
            it.sendMessage(mHandler)
        })
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        socket?.close()
        mScope.destory()
    }
}