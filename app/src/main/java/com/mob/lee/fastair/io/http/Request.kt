package com.mob.lee.fastair.io.http

import kotlinx.coroutines.channels.Channel
import java.lang.Exception
import java.nio.ByteBuffer

data class Request(@Method val method: Int, val url: String, val urlParams: Map<String, String>, val header: Map<String, String>) {
    var firstBody: ByteBuffer? = null
    var body: Channel<ByteBuffer>? = null

    fun urlParam(key: String) = urlParams.get(key)

    fun bodyLength() = header["Content-Length"]?.toLongOrNull()

    suspend fun consumeBody(): ByteBuffer? {
        return try {
            if (true == firstBody?.hasRemaining()) {
                firstBody
            } else {
                body?.receive().also { firstBody = it }
            }
        } catch (e: Exception) {
            null
        }
    }
}