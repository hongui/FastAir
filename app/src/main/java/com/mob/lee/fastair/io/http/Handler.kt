package com.mob.lee.fastair.io.http

import com.mob.lee.fastair.io.socket.Writer
import kotlinx.coroutines.channels.Channel
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

interface Handler {
    fun canHandleIt(request: Request):Boolean
    suspend fun handle(request: Request,channel:SocketChannel):Writer
}