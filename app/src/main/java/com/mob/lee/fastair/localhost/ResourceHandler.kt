package com.mob.lee.fastair.localhost

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mob.lee.fastair.R
import com.mob.lee.fastair.io.http.ByteResponse
import com.mob.lee.fastair.io.http.Handler
import com.mob.lee.fastair.io.http.Request
import com.mob.lee.fastair.io.socket.Writer
import java.io.ByteArrayOutputStream

class ResourceHandler(val context: Context):Handler {
    override fun canHandleIt(request: Request): Boolean {
        return request.url.endsWith("favicon.ico")
    }

    override suspend fun handle(request: Request): Writer {
        val options = BitmapFactory.Options()
        options.outWidth=48
        options.outHeight=48
        options.outMimeType="image/png"
        val input=BitmapFactory.decodeResource(context.resources,R.drawable.alipay,options)
        val stream=ByteArrayOutputStream()
        input.compress(Bitmap.CompressFormat.PNG,100,stream)
        return ByteResponse(stream.toByteArray(), "image/png")
    }
}