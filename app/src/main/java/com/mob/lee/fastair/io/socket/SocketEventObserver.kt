package com.mob.lee.fastair.io.socket

import java.net.InetSocketAddress

interface SocketEventObserver {
    fun onReady(inetSocketAddress: InetSocketAddress){}
    fun onStart(){}
    fun onStop(){}
}