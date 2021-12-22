package com.mob.lee.fastair.io

import java.lang.Exception
import java.net.InetSocketAddress
import java.nio.channels.*
import java.nio.channels.spi.AbstractSelectableChannel
import kotlin.concurrent.thread

class SocketFactory(val channel: Channel,port: Int,host: String?) {
    var mSelector:Selector?=null
    var mAcceptThread:Thread?=null
    var mReadThread:Thread?=null
    var mWriteThread:Thread?=null
    var mOnError:((Exception)->Boolean)?=null
    var timeout=1000L

    init {
        if(channel is ServerSocketChannel){
            accept(channel, port)
        }else{

        }
    }

    private fun accept(channel: ServerSocketChannel,port: Int){
        if(null!=mAcceptThread){
            return
        }
        mAcceptThread=thread {
            try {
                if(null==mSelector){
                    mSelector= Selector.open()
                }
                channel.configureBlocking(false)
                channel.socket().bind(InetSocketAddress(port))
                channel.register(mSelector!!,SelectionKey.OP_ACCEPT)

                while (true){
                    val size=mSelector?.select(timeout)
                    if(0==size){
                        continue
                    }
                    onSelected()
                }
            }catch (e:Exception){
                if(true==mOnError?.invoke(e)){
                    accept(channel, port)
                }
            }

        }
    }

    private fun onAccept(channel:ServerSocketChannel){
        channel.accept().run {
            configureBlocking(false)
            register(mSelector!!,SelectionKey.OP_READ)
        }
    }

    private fun onConnect(channel:SocketChannel){

    }

    private fun onRead(channel:SocketChannel){

    }

    private fun onWrite(channel:SocketChannel){

    }

    private fun onSelected(){
        val keys=mSelector?.selectedKeys()?.iterator()
        while (true==keys?.hasNext()){
            val key=keys.next()
            if(key.isAcceptable){
                onAccept(channel as ServerSocketChannel)
            }else if(key.isConnectable){
                onConnect(channel as SocketChannel)
            }else if(key.isReadable){
                onRead(key.attachment() as SocketChannel)
            }else if(key.isWritable){
                onWrite(key.attachment() as SocketChannel)
            }
            keys.remove()
        }
    }

    companion object{
        private const val TYPE_THREAD_ACCEPT="accept_thread"
        fun open(port: Int,host:String?=null):SocketFactory{
            return if(null==host){
                SocketFactory(ServerSocketChannel.open(),port, host)
            }else{
                SocketFactory(SocketChannel.open(),port, host)
            }
        }

    }
}