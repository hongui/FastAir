package com.mob.lee.fastair.localhost

import android.content.Context
import android.util.Log
import com.mob.lee.fastair.io.http.*
import com.mob.lee.fastair.io.socket.Writer

class HomeHandler(val context: Context) : Handler {
    override fun canHandleIt(request: Request) = true

    override suspend fun handle(request: Request): Writer {
        val name = request.url.removePrefix("/")
        if (name.isBlank()) {
            return ResourceResponse({ context.assets.open("index.html") }, HTML)
        }
        val ext = name.split(".").lastOrNull()
        val type = when (ext) {
            "css" -> CSS
            "js" -> JS
            "png" -> PNG
            "jpeg" -> JPEG
            else -> HTML
        }
        Log.d(TAG, "Open file at ${name},with extension ${ext}")
        return ResourceResponse({ context.assets.open(name.replace("_app", "app")) }, type)
    }

    companion object {
        const val TAG = "HomeHandler"
    }
}