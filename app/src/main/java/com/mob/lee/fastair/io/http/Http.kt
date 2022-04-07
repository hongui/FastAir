package com.mob.lee.fastair.io.http

import android.util.Log
import com.mob.lee.fastair.io.socket.AbstractChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

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

class Http(scope: CoroutineScope) : AbstractChannel(scope, ServerSocketChannel.open()) {
    private val handler = ArrayList<Handler>()


    override suspend fun onRead(channel: SocketChannel, buffer: Channel<ByteBuffer>) {
        val first=buffer.receive()
        val request = Parser.parseRequest(first)
        //这里需要再想想
        request ?: let {
            channel.close()
            return
        }

        request.body=if(first.hasRemaining()){
            val second = buffer.receive()
            val newBuffer=ByteBuffer.allocate(second.limit()+first.remaining())
            newBuffer.put(first.slice())
            newBuffer.put(second)
            val c = Channel<ByteBuffer>()
            c.send(newBuffer)
            buffer.consumeEach {
                do{
                    val result=c.trySendBlocking(it)
                }while (result.isFailure)
            }
            c
        }else{
            buffer
        }
        Log.d(TAG,"Accept request ${request}")
        dispatch(request, channel)
    }


    private fun dispatch(request: Request,channel: SocketChannel) {
        mScope.launch(Dispatchers.IO) {
            handler.forEach {
                if (it.canHandleIt(request)) {
                    Log.d(TAG, "Choose handler ${it} to handle ${request}")
                    val result = it.handle(request,channel)
                    try {
                        result(channel)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        //JsonResponse.json(null, SERVERERROR).invoke(channel)
                        Log.e(TAG,"----------------------------------------${e.printStackTrace()}")
                    }
                    return@launch
                }
            }
        }
    }

    fun addHandler(handler: Handler) = this.handler.add(0, handler)

    companion object {
        const val TAG = "Http"
    }
}