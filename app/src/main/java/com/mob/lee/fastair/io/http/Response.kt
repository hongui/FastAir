package com.mob.lee.fastair.io.http

import com.mob.lee.fastair.io.socket.Writer
import com.mob.lee.fastair.utils.buffer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONArray
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.*
import kotlin.collections.HashMap

abstract class Response(val status: Int):Writer {
    val header = HashMap<String, String>()
    abstract val contentType:String

    abstract fun length():Long

    abstract fun onWriteBody(channel: SocketChannel)

    override suspend fun invoke(channel: SocketChannel) {
        header.put("Content-Type", contentType)
        header.put("Date", Date().toString())
        header.put("Content-Length",length().toString())
        channel.write(toString().buffer())
        onWriteBody(channel)
    }

    override fun toString(): String {
        val value = "HTTP/1.1 ${status} ${status(status)}\n${header.entries.joinToString("\n", transform = { "${it.key}: ${it.value}" })}\n\r\n"
        return value
    }

    companion object {
        private const val TAG="HttpResponse"
    }
}

const val SUCCESS = 200
const val NOTFOUNT = 404
const val SERVERERROR = 500
fun status(code: Int) = when (code) {
    SUCCESS -> "OK"
    NOTFOUNT -> "Not Found"
    else -> "Internal Server Error"
}