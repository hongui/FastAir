package com.mob.lee.fastair.io.socket

import java.nio.channels.SocketChannel

open class ConnectListener {
    open fun onConfig(){}
    open fun onStart(){}
    open fun onConnect(channel: SocketChannel){}
    open fun onStop(){}
}