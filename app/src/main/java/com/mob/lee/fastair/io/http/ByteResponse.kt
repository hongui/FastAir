package com.mob.lee.fastair.io.http

import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class ByteResponse(action: () -> ByteArray,mime:String) : Response<ByteArray>(action,mime=mime) {

    override fun addHeader(source: ByteArray) {
        header.put("Content-Length",source.size.toString())
    }

    override fun onWriteBody(channel: SocketChannel,source:ByteArray) {
        channel.write(ByteBuffer.wrap(source))
    }
}