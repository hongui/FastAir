package com.mob.lee.fastair.io

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.channels.spi.AbstractSelectableChannel

class SocketFactory(val mScope: CoroutineScope, val mChannel: AbstractSelectableChannel) {
    var mSelector: Selector? = null
    var mOnError: ((Exception) -> Boolean)? = null
    var timeout = 1000L
    var mRunning=true

    private fun startLoop(port: Int, host: String?) {
        mScope.launch(Dispatchers.IO) {
            try {
                config(mChannel)

                loop()

                clear()
            } catch (e: Exception) {
                if (true == mOnError?.invoke(e)) {

                }
            }
        }
    }

    private fun config(channel: AbstractSelectableChannel) {
        if (null == mSelector) {
            mSelector = Selector.open()
        }
        channel.configureBlocking(false)
        if(channel is SocketChannel){
            channel.register(mSelector,SelectionKey.OP_CONNECT)
        }else{
            channel.register(mSelector,SelectionKey.OP_ACCEPT)
        }
    }

    private fun loop(){
        while (mRunning) {
            val size = mSelector?.select(timeout)
            if (0 == size) {
                continue
            }
            onSelected()
        }
    }


    private fun clear(){
        mSelector?.close()
        mSelector=null
        mChannel.close()
    }

    private fun onAccept(channel: ServerSocketChannel) {
        channel.accept().run {
            configureBlocking(false)
            register(mSelector!!, SelectionKey.OP_READ, this)
            register(mSelector!!, SelectionKey.OP_WRITE, this)
        }
    }

    private fun onConnect(channel: SocketChannel) {

    }

    private fun onRead(channel: SocketChannel) {
        mScope.launch(Dispatchers.IO) {
            val size=channel.socket().receiveBufferSize
            val buffer=ByteBuffer.allocate(size)
            var alreadyRecieve=0
            do{
                val s=channel.read(buffer)
                alreadyRecieve+=s
            }while (alreadyRecieve<size)
            val s=String(buffer.array(),0,size)
            Log.e("TAG",s)
        }
    }

    private fun onWrite(channel: SocketChannel) {

    }

    private fun onSelected() {
        val keys = mSelector?.selectedKeys()?.iterator()
        while (true == keys?.hasNext()) {
            val key = keys.next()
            if (key.isAcceptable) {
                onAccept(mChannel as ServerSocketChannel)
            } else if (key.isConnectable) {
                onConnect(mChannel as SocketChannel)
            } else if (key.isReadable) {
                onRead(key.attachment() as SocketChannel)
            } else if (key.isWritable) {
                onWrite(key.attachment() as SocketChannel)
            }
            keys.remove()
        }
    }

    companion object {
        private const val TYPE_THREAD_ACCEPT = "accept_thread"
        fun open(scope: CoroutineScope, port: Int, host: String? = null): SocketFactory {
            return if (null == host) {
                SocketFactory(scope, ServerSocketChannel.open())
            } else {
                SocketFactory(scope, SocketChannel.open())
            }.apply { startLoop(port, host) }
        }

    }
}