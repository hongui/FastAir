package com.mob.lee.fastair.service

import android.content.Intent
import com.mob.lee.fastair.io.ProcessListener
import com.mob.lee.fastair.io.SocketService
import com.mob.lee.fastair.io.StringReader
import com.mob.lee.fastair.io.StringWriter
import com.mob.lee.fastair.io.state.State
import kotlinx.coroutines.async

/**
 * Created by Andy on 2017/8/29.
 */
class MessageService : SocketSerice() {
    var socket: SocketService? = null
    var mMessageListener: ProcessListener? = null

    fun write(content: String) {
        mScope.async {
            socket?.write(StringWriter(content))
        }
    }

    override fun onReceiveMessage(message: State) {
        mMessageListener?.invoke(message)
    }

    override suspend fun onNewTask(intent: Intent?) {
    }

    override suspend fun connected(socket: SocketService) {
        socket.read(StringReader() {
            it.sendMessage(mHandler)
        })
    }
}