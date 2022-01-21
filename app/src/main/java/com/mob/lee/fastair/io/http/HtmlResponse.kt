package com.mob.lee.fastair.io.http

import android.content.res.AssetManager
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class HtmlResponse(val manager:AssetManager,val file:String,status:Int, override val contentType: String="text/html; charset=utf-8"):Response(status) {

    override fun length()=manager.openFd(file).length

    override fun onWriteBody(channel: SocketChannel) {
        manager.open(file).use {
            val buffer=ByteArray(8*1024)
            var count=it.read(buffer)
            while (count!=-1){
                channel.write(ByteBuffer.wrap(buffer,0,count))
                count=it.read(buffer)
            }
        }
    }
}