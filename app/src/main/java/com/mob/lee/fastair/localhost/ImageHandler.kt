package com.mob.lee.fastair.localhost

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import com.mob.lee.fastair.io.http.*
import com.mob.lee.fastair.io.socket.Writer
import kotlinx.coroutines.channels.Channel
import java.io.*
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class ImageHandler(val context: Context) : Handler {
    companion object {
        const val PREV = "/images"
    }

    override fun canHandleIt(request: Request) = request.url.startsWith(PREV)

    override suspend fun handle(request: Request, channel: SocketChannel): Writer {
        val path = request.url.substring(PREV.length)
        if (request.urlParams.containsKey("width")) {
            val width = request.urlParam("width")!!.toInt()
            val height = request.urlParam("height")!!.toInt()
            val id = request.urlParam("id")!!.toLong()
            try {
                val bitmap = fetchThumb(id, path, Size(width, height))
                ByteArrayOutputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                    val bytes=it.toByteArray()
                    return ByteResponse({bytes},PNG)
                }
            }catch (e:Exception){
                return JsonResponse.json(null, NOTFOUNT)
            }
        }
        return ResourceResponse({ FileInputStream(path) }, PNG)
    }

    fun fetchThumb(id: Long, path: String, size: Size): Bitmap {
        return if (Build.VERSION.SDK_INT >= 29) {
            ThumbnailUtils.createImageThumbnail(File(path), size, null)
        } else {
            MediaStore.Images.Thumbnails.getThumbnail(context.contentResolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, null)
        }
    }
}