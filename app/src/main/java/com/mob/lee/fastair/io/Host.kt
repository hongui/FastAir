package com.mob.lee.fastair.io

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel

class Host {
    var port:Int=0
    var ip:String=""
    var isHosting:Boolean=false
    var host:ServerSocketChannel?=null
    var selector:Selector?=null

    fun start(scope: CoroutineScope){
        if(null!=host){
            return
        }
        selector= Selector.open()
        host=ServerSocketChannel.open()
        val state=host?.run {
            configureBlocking(false)
            socket().bind(InetSocketAddress(port))
            register(selector,SelectionKey.OP_ACCEPT)
            scope.async(Dispatchers.IO) {
                while (true){
                    val se=selector?.select()
                    when(se){

                    }
                }
            }
        }
    }

    fun stop(){

    }

    private fun handleAccept(){

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