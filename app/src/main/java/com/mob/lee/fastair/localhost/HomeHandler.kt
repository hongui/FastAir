package com.mob.lee.fastair.localhost

import android.content.Context
import com.mob.lee.fastair.io.http.Handler
import com.mob.lee.fastair.io.http.HtmlResponse
import com.mob.lee.fastair.io.http.Request
import com.mob.lee.fastair.io.http.SUCCESS
import com.mob.lee.fastair.io.socket.Writer

class HomeHandler(val context: Context) : Handler {
    override fun canHandleIt(request: Request) = true

    override suspend fun handle(request: Request): Writer {
        val html=HtmlResponse(context.assets,"index.html",SUCCESS)
        return html
    }
}