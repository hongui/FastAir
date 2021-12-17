package com.mob.lee.fastair.io

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel

class Host {
    var port:Int=0
    var ip:String=""
    var isHosting:Boolean=false
    var host:ServerSocketChannel?=null
    var selector:Selector?=null

    fun start(lifecycleOwner: LifecycleOwner){
        if(null!=host){
            return
        }
        selector= Selector.open()
        host=ServerSocketChannel.open()
        host?.run {
            configureBlocking(false)
            socket().bind(InetSocketAddress(port))
            register(selector,SelectionKey.OP_ACCEPT)
        }
    }

    fun stop(){

    }

    private fun socket()=host?.socket()

    companion object{
        fun host(h:Host.()->Unit):Host{
            val host=Host()
            h(host)
            return host
        }
    }
}