package com.mob.lee.fastair.io.socket

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.channels.spi.AbstractSelectableChannel

class SocketFactory(val mScope: CoroutineScope, val mChannel: AbstractSelectableChannel) {
    var mSelector: Selector? = null
    var mDispatcher = SocketDispatcher()
    var mOnError: ((Exception) -> Boolean)? = null
    var timeout = 1000L
    var mRunning = true

    private fun startLoop(inet: InetSocketAddress) {
        mScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Begin config ${inet.toString()}")
                config(mChannel, inet)
                Log.d(TAG, "End config")

                Log.d(TAG, "Begin loop")
                loop()
                Log.d(TAG, "End loop")

                Log.d(TAG, "Begin clear")
                clear()
                Log.d(TAG, "End clear")
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                if (true == mOnError?.invoke(e)) {

                }
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
            mDispatcher.dispatch(READY, inet)
        } else {
            channel.register(mSelector, SelectionKey.OP_CONNECT, inet)
        }
        mDispatcher.dispatch(START)
    }

    private fun loop() {
        while (mRunning) {
            val size = mSelector?.select(timeout)
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
        mRunning = false
        mDispatcher.dispatch(STOP)
    }

    private fun onAccept(key: SelectionKey, channel: ServerSocketChannel) {
        var c: SocketChannel? = null
        val start = System.currentTimeMillis()
        do {
            c = channel.accept()
        } while (c == null)
        Log.d(TAG, "Wait accept for ${System.currentTimeMillis() - start}ms,channel=${c}")
        c.run {
            configureBlocking(false)
            register(mSelector!!, SelectionKey.OP_READ, this)
            register(mSelector!!, SelectionKey.OP_WRITE, this)
        }
    }

    private fun onConnect(key: SelectionKey, channel: SocketChannel) = mScope.launch(Dispatchers.IO) {
        val inet = key.attachment() as InetSocketAddress
        channel.connect(inet)
        channel.register(mSelector!!, SelectionKey.OP_READ, this)
        channel.register(mSelector!!, SelectionKey.OP_WRITE, this)
        withContext(Dispatchers.Main) {
            mDispatcher.dispatch(READY, inet)
        }
    }

    private fun onRead(channel: SocketChannel) {
        mScope.launch(Dispatchers.IO) {
            val buffer = ByteBuffer.allocate(8 * 1024)
            val size = channel.read(buffer)
            buffer.flip()
            val s = String(buffer.array(), 0, size)
            Log.e(TAG, s)
        }
    }

    private fun onWrite(channel: SocketChannel) {

    }

    private fun onSelected() {
        val keys = mSelector?.selectedKeys()?.iterator()
        while (true == keys?.hasNext()) {
            val key = keys.next()
            when (key.readyOps()) {
                SelectionKey.OP_ACCEPT -> onAccept(key, mChannel as ServerSocketChannel)
                SelectionKey.OP_CONNECT -> onConnect(key, mChannel as SocketChannel)
                SelectionKey.OP_READ -> onRead(key.attachment() as SocketChannel)
                SelectionKey.OP_WRITE -> onRead(key.attachment() as SocketChannel)
            }
            keys.remove()
        }
    }

    companion object {
        const val TAG = "SocketFactory"
        fun open(scope: CoroutineScope, port: Int, host: String? = null): SocketFactory {
            return if (null == host) {
                SocketFactory(scope, ServerSocketChannel.open()) to InetSocketAddress(port)
            } else {
                SocketFactory(scope, SocketChannel.open()) to InetSocketAddress(host, port)
            }.run {
                first.startLoop(second)
                first
            }
        }

    }
}