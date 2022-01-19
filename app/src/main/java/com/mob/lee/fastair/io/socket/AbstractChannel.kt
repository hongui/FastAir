package com.mob.lee.fastair.io.socket

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.withIndex
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
    private val mWriters = Channel<Writer>(Channel.UNLIMITED)

    fun startLoop(inet: InetSocketAddress) {
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
                onError(e)
                stop()
            }finally {
                onEnd()
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
        while (mRunning) {
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
        if(!mRunning){
            return
        }
        mRunning = false
        mSelector?.keys()?.forEach {
            val channel=it.attachment()
            try {
                when(channel){
                    is SocketChannel->channel.finishConnect()

                    is ServerSocketChannel->channel.socket().close()
                }
                onDisconnected()
            }catch (e:Exception){

            }

        }
    }

    fun write(writer: Writer)=mScope.launch {
        mWriters.send(writer)
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
            onConnected()
        }
    }

    private fun onRead(channel: SocketChannel) {
        Log.e(TAG,"===========+++=============")
        mScope.launch(Dispatchers.IO) {
            while (mRunning){
                Log.e(TAG,"========================")
                val buffer = ByteBuffer.allocate(8 * 1024)
                try {
                    channel.read(buffer)
                }catch (e:Exception){
                    onError(e)
                }
                buffer.flip()
                onRead(channel, buffer)
            }
        }
    }

    private fun onWrite(channel: SocketChannel){
        /*mScope.launch(Dispatchers.IO) {
            while (mRunning){
                val writer = mWriters.receive()
                try {
                    writer.collect {
                        it.flip()
                        channel.write(it)
                    }
                }catch (e:Exception){
                    onError(e)
                }

            }
        }*/
    }
    private fun onSelected() {
        val keys = mSelector?.selectedKeys()?.iterator()
        while (true == keys?.hasNext()) {
            val key = keys.next()
            when (key.readyOps()) {
                SelectionKey.OP_ACCEPT -> onAccept(key, mChannel as ServerSocketChannel)
                SelectionKey.OP_CONNECT -> onConnect(key, mChannel as SocketChannel)
                SelectionKey.OP_READ -> onRead(key.attachment() as SocketChannel)
                SelectionKey.OP_WRITE -> onWrite(key.attachment() as SocketChannel)
            }
            keys.remove()
        }
    }

    open fun onStart(address: InetSocketAddress){}
    open fun onConnected(){}
    abstract fun onRead(channel: SocketChannel,buffer:ByteBuffer)
    open fun onDisconnected(){}
    open fun onEnd(){}
    open fun onError(exception: Exception){
        print(exception)
    }

    companion object {
        const val TAG = "SocketFactory"
    }
}