package com.mob.lee.fastair.io.http

import android.util.Log
import com.mob.lee.fastair.io.socket.AbstractChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.channels.spi.AbstractSelectableChannel

/**
 * GET /home.html HTTP/1.1
 *Host: developer.mozilla.org
 *User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:50.0) Gecko/20100101 Firefox/50.0
 *Accept: text/html,application/xhtml+xml,application/xml;q=0.9;q=0.8
 *Accept-Language: en-US,en;q=0.5
 *Accept-Encoding: gzip, deflate, br
 *Referer: https://developer.mozilla.org/testpage.html
 *Connection: keep-alive
 *Upgrade-Insecure-Requests: 1
 *If-Modified-Since: Mon, 18 Jul 2016 02:36:04 GMT
 *If-None-Match: "c561c68d0ba92bbeb8b0fff2a9199f722e3a621a"
 *Cache-Control: max-age=0
 */

class Http(scope: CoroutineScope): AbstractChannel(scope, ServerSocketChannel.open()) {
    private val handler= ArrayList<Handler>()


    override fun onRead(channel: SocketChannel, buffer: ByteBuffer) {
        val request = String(buffer.array(), 0, buffer.limit())

        Log.d(TAG,request)
        val sequences=request.lineSequence()
        val head=sequences.firstOrNull()?.let { parseStatusLine(it) }
        //这里需要再想想
        head?:return
        dispatch(Request(httpMethod(head.first),head.second),channel)
    }

    private fun parseStatusLine(line:String):Pair<String,String>?{
        val values = line.split(Regex("\\s"))
        if(values.size==3){
            return values[0] to values[1]
        }
        return null
    }

    private fun dispatch(request:Request,channel: SocketChannel){
        mScope.launch(Dispatchers.IO) {
            handler.forEach {
                if(it.canHandleIt(request)){
                    Log.d(TAG,"Choose handler ${it} to handle ${request}")
                    val result=it.handle(request)
                    result(channel)
                    return@launch
                }
            }
        }
    }

    fun addHandler(handler: Handler)=this.handler.add(0,handler)

    companion object{
        const val TAG="Http"
    }
}