package com.mob.lee.fastair.localhost

import com.mob.lee.fastair.io.http.*
import com.mob.lee.fastair.io.socket.Writer
import java.io.FileInputStream

class DownloadHandler:Handler {
    companion object {
        const val PREV = "/downloads"
    }

    override fun canHandleIt(request: Request) = request.url.startsWith(PREV)

    override suspend fun handle(request: Request): Writer {
        val path = request.url.substring(PREV.length)
        return ResourceResponse({ FileInputStream(path) }, OCTET)
    }
}