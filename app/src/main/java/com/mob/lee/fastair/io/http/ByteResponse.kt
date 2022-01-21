package com.mob.lee.fastair.io.http

import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class ByteResponse(val byte:ByteArray, override val contentType: String):Response(SUCCESS) {
    override fun length(): Long=byte.size.toLong()

    override fun onWriteBody(channel: SocketChannel) {
        channel.write(ByteBuffer.wrap(byte))
    }
}