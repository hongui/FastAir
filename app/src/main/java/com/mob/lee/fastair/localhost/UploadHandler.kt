package com.mob.lee.fastair.localhost

import android.content.Context
import com.mob.lee.fastair.io.http.*
import com.mob.lee.fastair.io.socket.Writer
import com.mob.lee.fastair.utils.createFile
import kotlinx.coroutines.channels.consumeEach
import java.nio.channels.SocketChannel

class UploadHandler(val context: Context) : Handler {

    override fun canHandleIt(request: Request) = request.url == "/upload" && POST == request.method

    override suspend fun handle(request: Request, channel: SocketChannel): Writer {
        val buffer = request.body.receive()
        val part=Parser.parsePart(buffer)
        part?:return JsonResponse.json(null, NOTFOUNT)

        val fileName = part.first.split(";").find { it.contains("filename") }
        fileName ?: return JsonResponse.json(null, NOTFOUNT)
        val name = fileName.split("=")[1].trim().trim('"')

        context.createFile(name).outputStream().use { file ->
            request.body.consumeEach {
                file.write(it.array(), it.position(), it.remaining())
            }
        }
        return JsonResponse.json(null, SUCCESS)
    }
}