package com.mob.lee.fastair.localhost

import android.util.Log
import com.mob.lee.fastair.io.http.Handler
import com.mob.lee.fastair.io.http.Request
import com.mob.lee.fastair.io.http.ResourceResponse
import com.mob.lee.fastair.io.socket.Writer
import java.io.FileInputStream

class ImageHandler:Handler {
    companion object{
        const val PREV="/images"
    }
    override fun canHandleIt(request: Request)=request.url.startsWith(PREV)

    override suspend fun handle(request: Request): Writer {
        val path=request.url.substring(PREV.length)
        Log.e("TAG","---------------${path}")
        return ResourceResponse(FileInputStream(path),"image/jpeg")
    }
}