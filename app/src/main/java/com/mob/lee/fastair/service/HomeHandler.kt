package com.mob.lee.fastair.service

import android.content.Context
import com.mob.lee.fastair.io.http.Handler
import com.mob.lee.fastair.io.http.Request
import com.mob.lee.fastair.io.http.Response
import com.mob.lee.fastair.io.socket.Writer
import com.mob.lee.fastair.utils.buffer
import kotlinx.coroutines.flow.flow
import java.nio.ByteBuffer

class HomeHandler(val context: Context) : Handler {
    override fun canHandleIt(request: Request) = true

    override suspend fun handle(request: Request): Writer {
        return flow<ByteBuffer> {
            val success = Response.success()
            emit(success.toString().buffer())
            context.assets.open("404.html").use {
                val buffer = ByteArray(8 * 1024)
                var count = -1
                do {
                    count = it.read(buffer)
                    emit(ByteBuffer.wrap(buffer, 0, count))
                } while (count > 0)

            }
        }
    }
}