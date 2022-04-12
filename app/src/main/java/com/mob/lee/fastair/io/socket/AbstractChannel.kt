package com.mob.lee.fastair.io.socket

import android.util.Log
import kotlinx.coroutines.*
import java.net.BindException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.channels.spi.AbstractSelectableChannel

abstract class AbstractChannel(
    val mScope: CoroutineScope,
    val mChannel: AbstractSelectableChannel
) {
    var mTimeout = 1000L
    var mRunning = false
    var mTime=0
    var mListener:ConnectListener?=null
    private var mSelector: Selector? = null

    fun startLoop(inet: InetSocketAddress) {
        Log.d(TAG,"Call loop")
        mScope.launch(Dispatchers.IO) {
            try {
                mListener?.let {
                    withContext(Dispatchers.Main){
                        it.onConfig()
                    }
                }
                Log.d(TAG, "Begin config ${inet.toString()}")
                config(mChannel, inet)
                Log.d(TAG, "End config")

                mListener?.let {
                    withContext(Dispatchers.Main){
                        it.onStart()
                    }
                }
                Log.d(TAG, "Begin loop")
                mRunning=true
                loop()
                Log.d(TAG, "End loop")

                mListener?.let {
                    withContext(Dispatchers.Main){
                        it.onStop()
                    }
                }
                Log.d(TAG, "Begin finish")
                finish()
                Log.d(TAG, "End finish")
            } catch (e: BindException) {
                if(mTime>3){
                    Log.e(TAG, "Port ${inet.port} has used,exit!")
                    finish()
                    mTime=0
                }else{
                    Log.d(TAG, "Port ${inet.port} has used,retry!")
                    delay(1000)
                    startLoop(inet)
                    mTime+=1
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                onError(e)
                finish()
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
    }

    private fun loop() {
        while (mRunning) {
            val size = mSelector?.select(mTimeout)
            if (0 == size) {
                continue
            }
            onSelected()
        }
    }

    private fun finish() {
        mRunning=false
        Log.d(TAG, "Begin clear")
        clear()
        Log.d(TAG, "End clear")
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
    }

    private fun onAccept(key: SelectionKey, channel: ServerSocketChannel) =
        mScope.launch(Dispatchers.IO) {
            var c: SocketChannel? = null
            val start = System.currentTimeMillis()
            do {
                c = channel.accept()
            } while (c == null&&mRunning)
            if(!mRunning){
                Log.e(TAG,"-----Close-----")
                channel.close()
                return@launch
            }
            Log.d(TAG, "Wait accept for ${System.currentTimeMillis() - start}ms,channel=${c}")
            connected(c!!)
        }

    private fun onConnect(key: SelectionKey, channel: SocketChannel) =
        mScope.launch(Dispatchers.IO) {
            val inet = key.attachment() as InetSocketAddress
            channel.connect(inet)
            connected(channel)
        }

    private suspend fun connected(channel: SocketChannel) {
        withContext(Dispatchers.Main) {
            mListener?.onConnect(channel)
        }
        channel.configureBlocking(false)
        channel.register(mSelector, SelectionKey.OP_READ, channel)
    }

    private fun onRead(key: SelectionKey, channel: SocketChannel) {
        Log.d(TAG, "=============={Begin onRead")
        try {
            do {
                val buffer = read(channel)
                send(channel, buffer)
            } while (null != buffer)
        } catch (e: Exception) {
            onError(e)
            channel.close()
        } finally {
            Log.d(TAG, "finished onRead}==============")
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
        val buffer = ByteBuffer.allocate(1024 * 1024)
        val count = channel.read(buffer)
        return if (-1 == count || 0 == count) {
            null
        } else {
            buffer
        }
    }

    fun send(channel: SocketChannel, buffer: ByteBuffer?) {
        buffer?.let {
            it.flip()
            onRead(channel, it)
            Log.d(TAG, "send ${it.remaining()} byte data")
        }

    }

    open fun onError(exception: Exception) {
        print(exception)
    }

    abstract fun onRead(channel: SocketChannel, buffer: ByteBuffer)

    open fun close(channel: SocketChannel) {

    }

    companion object {
        const val TAG = "SocketFactory"
    }
}