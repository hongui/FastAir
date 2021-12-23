package com.mob.lee.fastair.io.socket

import java.net.InetSocketAddress

class SocketDispatcher {
    val observers=ArrayList<SocketEventObserver>()

    fun add(observer: SocketEventObserver)=observers.add(observer)

    fun remove(observer: SocketEventObserver)=observers.remove(observer)

    fun dispatch(@SocketEvent event: Int, arg:Any?=null){
        observers.forEach{
            when(event){
                READY->it.onReady(arg as InetSocketAddress)
                START->it.onStart()
                STOP->it.onStop()
            }
        }
    }
}