package com.mob.lee.fastair.localhost

import android.content.Context
import android.util.Log
import com.mob.lee.fastair.io.http.*
import com.mob.lee.fastair.io.socket.Writer
import com.mob.lee.fastair.utils.createFile
import java.nio.channels.SocketChannel

class UploadHandler(val context: Context) : Handler {

    override fun canHandleIt(request: Request) = request.url == "/upload" && POST == request.method

    override suspend fun handle(request: Request, channel: SocketChannel): Writer {
        var size = request.bodyLength() ?: 0
        val buffer = request.consumeBody()
        buffer ?: return JsonResponse.json(null, NOTFOUNT)

        val before = buffer.position()
        val part = Parser.parsePart(buffer)
        part ?: return JsonResponse.json(null, NOTFOUNT)
        val after = buffer.position()

        size -= after - before

        Log.e("SocketFactory", "=========${request.bodyLength()}")
        val fileName = part.second.split(";").find { it.contains("filename") }
        fileName ?: return JsonResponse.json(null, NOTFOUNT)
        val name = fileName.split("=")[1].trim().trim('"')

        context.createFile(name).outputStream().use { file ->
            do {
                request.consumeBody()?.also {
                    size -= it.remaining()
                    file.write(it.array(), it.position(), it.remaining())
                    it.position(it.limit())
                    Log.e("SocketFactory", "=================$size==================")
                }
            } while (size > 0)
        }
        return JsonResponse.json(null, SUCCESS)
    }
}