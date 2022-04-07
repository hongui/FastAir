package com.mob.lee.fastair.io.http

import kotlinx.coroutines.channels.Channel
import java.nio.ByteBuffer

data class Request(@Method val method: Int, val url: String, val urlParams: Map<String, String>, val header: Map<String, String>) {
    lateinit var body: Channel<ByteBuffer>

    fun urlParam(key: String) = urlParams.get(key)

    fun bodyLength()=header["Content-Length"]?.toLongOrNull()
}