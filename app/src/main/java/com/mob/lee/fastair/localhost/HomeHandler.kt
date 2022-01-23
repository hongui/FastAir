package com.mob.lee.fastair.localhost

import android.content.Context
import android.util.Log
import com.mob.lee.fastair.io.http.Handler
import com.mob.lee.fastair.io.http.Request
import com.mob.lee.fastair.io.http.ResourceResponse
import com.mob.lee.fastair.io.socket.Writer

class HomeHandler(val context: Context) : Handler {
    override fun canHandleIt(request: Request) = true

    override suspend fun handle(request: Request): Writer {
        val name = request.url.removePrefix("/")
        if(name.isBlank()){
            return ResourceResponse( context.assets.open("index.html"))
        }
        val ext = name.split(".").lastOrNull()
        val type = when (ext) {
            "css" -> CSS
            "js" -> JS
            "png"-> IMAGE
            else -> HTML
        }
        Log.d(TAG,"Open file at ${name},with extension ${ext}")
        val stream = context.assets.open(name.replace("_app","app"))
        val html = ResourceResponse(stream, type)
        return html
    }

    companion object {
        const val TAG = "HomeHandler"
        const val HTML = "text/html; charset=utf-8"
        const val JS = "application/javascript; charset=utf-8"
        const val CSS = "text/css; charset=utf-8"
        const val IMAGE = "image/png"
    }
}