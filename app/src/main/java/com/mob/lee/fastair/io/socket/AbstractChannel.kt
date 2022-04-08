package com.mob.lee.fastair.io.socket

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.channels.spi.AbstractSelectableChannel

abstract class AbstractChannel(val mScope: CoroutineScope, val mChannel: AbstractSelectableChannel) {
    var mTimeout = 1000L
    var mRunning = true
    private var mSelector: Selector? = null

    fun startLoop(inet: InetSocketAddress) {
        mScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Begin config ${inet.toString()}")
                config(mChannel, inet)
                Log.d(TAG, "End config")

                Log.d(TAG, "Begin loop")
                loop()
                Log.d(TAG, "End loop")

            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                onError(e)
                stop()
            }
        }
    }

    private fun config(channel: AbstractSelectableChannel, inet: InetSocketAddress) {
        if (null == mSelector) {
            mSelector = Selector.open()
        }
        channel.configureBlocking(false)
        if (channel is ServerSocketChannel) {
            channel.socket().bind(inet)
            channel.register(mSelector, SelectionKey.OP_ACCEPT, inet)
        } else {
            channel.register(mSelector, SelectionKey.OP_CONNECT, inet)
        }
        onStart(inet)
    }

    private fun loop() {
        while (true) {
            val size = mSelector?.select(mTimeout)
            if (0 == size) {
                continue
            }
            onSelected()
        }
    }

    private fun clear() {
        mSelector?.close()
        mSelector = null
        mChannel.close()
    }

    fun stop() {
        if (!mRunning) {
            return
        }
        mRunning = false
        mSelector?.keys()?.forEach {
            val channel = it.attachment()
            try {
                when (channel) {
                    is SocketChannel -> channel.finishConnect()

                    is ServerSocketChannel -> channel.socket().close()
                }
                onDisconnected()
            } catch (e: Exception) {

            }
        }
        Log.d(TAG, "Begin clear")
        clear()
        Log.d(TAG, "End clear")

        onEnd()
    }

    private fun onAccept(key: SelectionKey, channel: ServerSocketChannel) = mScope.launch(Dispatchers.IO) {
        var c: SocketChannel? = null
        val start = System.currentTimeMillis()
        do {
            c = channel.accept()
        } while (c == null)
        Log.d(TAG, "Wait accept for ${System.currentTimeMillis() - start}ms,channel=${c}")
        connected(c)
    }

    private fun onConnect(key: SelectionKey, channel: SocketChannel) = mScope.launch(Dispatchers.IO) {
        val inet = key.attachment() as InetSocketAddress
        channel.connect(inet)
        connected(channel)
    }

    private suspend fun connected(channel: SocketChannel) {
        withContext(Dispatchers.Main) {
            onConnected()
        }
        channel.configureBlocking(false)
        channel.register(mSelector, SelectionKey.OP_READ)
    }

    private fun onRead(key: SelectionKey, channel: SocketChannel) {
        mScope.launch(Dispatchers.IO) {
            val channelBuffer = Channel<ByteBuffer>(10)
            try {
                do {
                    val buffer = read(channel)?.also {
                        Log.d(TAG, "Read ${it.remaining()} byte data")
                        send(it, channelBuffer)
                    }
                } while (null != buffer)
            } catch (e: Exception) {
                onError(e)
            }finally {
                channelBuffer.close()
                channel.close()
            }
        }
    }

    private fun onSelected() {
        val keys = mSelector?.selectedKeys()?.iterator()
        while (true == keys?.hasNext()) {
            val key = keys.next()
            when (key.readyOps()) {
                SelectionKey.OP_ACCEPT -> onAccept(key, mChannel as ServerSocketChannel)
                SelectionKey.OP_CONNECT -> onConnect(key, mChannel as SocketChannel)
                SelectionKey.OP_READ -> onRead(key, key.channel() as SocketChannel)
            }
            keys.remove()
        }
    }

    fun read(channel: SocketChannel): ByteBuffer? {
        val buffer = ByteBuffer.allocate(10 * 1024)
        var count = 0
        do {
            count = channel.read(buffer)
        } while (0 == count)

        return if (-1 == count) {
            null
        } else {
            buffer
        }
    }

    fun send(buffer: ByteBuffer, channel: Channel<ByteBuffer>) {
        buffer.flip()
        channel.trySendBlocking(buffer).onFailure {
            send(buffer, channel)
        }
    }

    open fun onError(exception: Exception) {
        print(exception)
    }

    open fun onStart(address: InetSocketAddress) {}
    open fun onConnected() {}
    abstract suspend fun onRead(channel: SocketChannel, buffer: Channel<ByteBuffer>)
    open fun onDisconnected() {}
    open fun onEnd() {}

    open fun close(channel: SocketChannel) {

    }

    companion object {
        const val TAG = "SocketFactory"
    }
}