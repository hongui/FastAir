package com.mob.lee.fastair.io.http

import android.util.Log
import com.mob.lee.fastair.io.socket.AbstractChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
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
    private val mCurrentChannel = HashMap<SocketChannel, Channel<ByteBuffer>>()

    override fun onRead(channel: SocketChannel, buffer: ByteBuffer) {
        mCurrentChannel.get(channel)?.let {
            dispatch(channel, buffer = buffer)
        } ?: Channel<ByteBuffer>(10).apply {
            val req = firstRequest(this, buffer)
            req ?: return channel.close()
            dispatch(channel, req)
        }
    }


    private fun firstRequest(channel: Channel<ByteBuffer>, buffer: ByteBuffer): Request? {
        val request = Parser.parseRequest(buffer)
        //这里需要再想想
        request ?: return null
        request.body = channel
        if (buffer.hasRemaining()) {
            request.firstBody = buffer
        }
        Log.d(TAG, "Accept request ${request}")
        return request
    }

    private fun dispatch(channel: SocketChannel, request: Request? = null, buffer: ByteBuffer? = null) {
        mCurrentChannel.get(channel)?.let { ch ->
            dispatchLeftBuffer(channel, ch, buffer)
            return
        }
        mScope.launch(Dispatchers.IO) {
            request?.let {
                dispatchFirstRequest(channel, it)
            }
        }
    }

    private fun dispatchLeftBuffer(socketChannel: SocketChannel, channel: Channel<ByteBuffer>, buffer: ByteBuffer? = null) {
        buffer?.let {
            val result = channel.trySendBlocking(it)
            if (result.isSuccess) {
                return
            } else {
                dispatchLeftBuffer(socketChannel, channel, buffer)
            }
        }
    }

    private suspend fun dispatchFirstRequest(channel: SocketChannel, request: Request) {
        handler.forEach {
            if (it.canHandleIt(request)) {
                Log.d(TAG, "Choose handler ${it} to handle ${request}")
                mCurrentChannel.put(channel, request.body!!)
                val result = it.handle(request, channel)
                try {
                    result(channel)
                    Log.d(TAG, "---------------Handler Success------------------")
                } catch (e: Exception) {
                    e.printStackTrace()
                    //JsonResponse.json(null, SERVERERROR).invoke(channel)
                    Log.e(TAG, "----------------------------------------${e.printStackTrace()}")
                } finally {
                    mCurrentChannel.remove(channel)
                    request.body?.close()
                    delay(1000)
                    channel.close()
                }
                return
            }
        }
    }

    fun addHandler(handler: Handler) = this.handler.add(0, handler)

    companion object {
        const val TAG = "Http"
    }
}