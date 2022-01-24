package com.mob.lee.fastair.io.http

import com.mob.lee.fastair.io.socket.Writer
import com.mob.lee.fastair.utils.buffer
import java.nio.channels.SocketChannel
import java.util.*
import kotlin.collections.HashMap

abstract class Response<T>(val action:()->T,val status: Int= SUCCESS,val mime: String= TEXT):Writer {
    val header = HashMap<String, String>()

    abstract fun onWriteBody(channel: SocketChannel,source:T)
    abstract fun onLength(source:T):Long
    init {
        header.put("Content-Type",mime)
    }

    override fun invoke(channel: SocketChannel) {
        val a=action()
        header.put("Date", Date().toString())
        header.put("Content-Length",onLength(a).toString())
        channel.write(toString().buffer())
        onWriteBody(channel,a)
    }

    override fun toString(): String {
        val value = "HTTP/1.1 ${status} ${status(status)}\n${header.entries.joinToString("\n", transform = { "${it.key}: ${it.value}" })}\n\r\n"
        return value
    }

    companion object {
        private const val TAG="HttpResponse"
    }
}