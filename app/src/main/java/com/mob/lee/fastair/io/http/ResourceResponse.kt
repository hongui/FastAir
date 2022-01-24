package com.mob.lee.fastair.io.http

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class ResourceResponse(action:()->InputStream,mime:String) :Response<InputStream>(action,mime=mime) {

    override fun onWriteBody(channel: SocketChannel,source:InputStream) {
        source.use {
            val buffer=ByteArray(8*1024)
            var count=it.read(buffer)
            while (count!=-1){
                channel.write(ByteBuffer.wrap(buffer, 0, count))
                count=it.read(buffer)
            }
        }
    }

    override fun onLength(source: InputStream)=source.available().toLong()
}