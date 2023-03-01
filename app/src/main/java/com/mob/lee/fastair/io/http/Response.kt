package com.mob.lee.fastair.io.http

import com.mob.lee.fastair.io.socket.Writer
import com.mob.lee.fastair.utils.buffer
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.*
import kotlin.collections.HashMap

abstract class Response<T>(val action:()->T,val status: Int= SUCCESS,val mime: String= TEXT):Writer {
    val header = HashMap<String, String>()

    abstract fun onWriteBody(channel: SocketChannel,source:T)

    open fun addHeader(source: T){}

    init {
        header.put("Content-Type",mime)
    }

    override fun invoke(channel: SocketChannel) {
        val a=action()
        header.put("Date", Date().toString())
        addHeader(a)
        channel.write(toString().buffer())
        return onWriteBody(channel,a)
    }

    fun write(channel: SocketChannel,buffer:ByteArray,length:Int){
        val buf=ByteBuffer.wrap(buffer,0,length)
        var total=0L
        while (total!=length.toLong()){
            total+=channel.write(buf)
        }
    }

    fun write(channel: SocketChannel,buffer:ByteBuffer){
        while (buffer.hasRemaining()){
            channel.write(buffer)
        }
    }
    override fun toString(): String {
        val value = "HTTP/1.1 ${status} ${status(status)}\n${header.entries.joinToString("\n", transform = { "${it.key}: ${it.value}" })}\n\r\n"
        return value
    }

    companion object {
        private const val TAG="HttpResponse"
    }
}