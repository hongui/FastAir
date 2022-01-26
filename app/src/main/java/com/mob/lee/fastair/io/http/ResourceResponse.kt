package com.mob.lee.fastair.io.http

import java.io.InputStream
import java.nio.channels.SocketChannel

class ResourceResponse(action: () -> InputStream, mime: String) : Response<InputStream>(action, mime = mime) {

    override fun addHeader(source: InputStream) {
        header.put("Content-Length",source.available().toString())
    }

    override fun onWriteBody(channel: SocketChannel, source: InputStream) {
        source.use {
            val buffer = ByteArray(8 * 1024)
            var count = it.read(buffer)
            while (count != -1) {
                write(channel,buffer,count)
                count = it.read(buffer)
            }
        }
    }
}