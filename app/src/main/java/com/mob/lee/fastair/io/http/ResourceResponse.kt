package com.mob.lee.fastair.io.http

import android.util.Log
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class ResourceResponse(val stream: InputStream,override val contentType: String="text/html; charset=utf-8") :Response(SUCCESS) {

    override fun length(): Long =stream.available().toLong()

    override fun onWriteBody(channel: SocketChannel) {
        Log.e("HomeHandler",stream.toString())
        stream.use {
            val buffer=ByteArray(8*1024)
            var count=it.read(buffer)
            while (count!=-1){
                channel.write(ByteBuffer.wrap(buffer, 0, count))
                count=it.read(buffer)
            }
        }
    }
}