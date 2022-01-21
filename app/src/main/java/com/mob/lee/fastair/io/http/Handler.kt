package com.mob.lee.fastair.io.http

import com.mob.lee.fastair.io.socket.Writer

interface Handler {
    fun canHandleIt(request: Request):Boolean
    suspend fun handle(request: Request):Writer
}