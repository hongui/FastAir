package com.mob.lee.fastair.service

import android.content.Intent
import com.mob.lee.fastair.io.ProgressListener
import com.mob.lee.fastair.io.ProtocolByte
import com.mob.lee.fastair.io.StringWriter
import com.mob.lee.fastair.io.state.MessageState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.channels.SocketChannel

/**
 * Created by Andy on 2017/8/29.
 */
class MessageService : TransferService() {
    var mMessageListener: ProgressListener? = null
    override var port: Int=9528

    fun write(content: String) {
        write(StringWriter(content))
    }

    override suspend fun onNewTask(intent: Intent?) {
    }

    override fun onReadFinished(channel: SocketChannel, data: ProtocolByte) {
        launch(Dispatchers.Main) {
            mMessageListener?.invoke(MessageState(data.getString(),System.currentTimeMillis()))
        }
    }
}