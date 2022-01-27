package com.mob.lee.fastair.localhost

import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.mob.lee.fastair.io.http.Handler
import com.mob.lee.fastair.io.http.JsonResponse
import com.mob.lee.fastair.io.http.Request
import com.mob.lee.fastair.io.socket.Writer
import org.json.JSONArray

class ChatHandler(val context:Context):Handler {
    companion object {
        const val PREV = "/chatinfo"
    }

    init {

    }
    override fun canHandleIt(request: Request) = request.url.startsWith(PREV)

    override suspend fun handle(request: Request): Writer {
        Log.e("TAG",request.body)

        val obj=JSONArray()

        val manager = ContextCompat.getSystemService(context, ClipboardManager::class.java)
        manager?.primaryClip?.itemCount?.let {
            for (i in 0 until it){
                val item=manager.primaryClip?.getItemAt(i)
                item?.text?.let { obj.put(it) }
            }
        }

        return JsonResponse.json(obj)
    }
}