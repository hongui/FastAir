package com.mob.lee.fastair.io.http

import com.mob.lee.fastair.utils.buffer
import org.json.JSONArray
import org.json.JSONObject
import java.nio.channels.SocketChannel

class JsonResponse(status:Int, override val contentType: String="application/json"):Response(status) {
    private var obj=JSONObject()

    override fun length()=obj.toString().length.toLong()

    fun json(data: JSONArray, code:Int=0, message:String="Success") {
        obj.put("code",code)
        obj.put("msg", message)
        obj.put("data",data)
    }

    override fun onWriteBody(channel: SocketChannel) {
        channel.write(obj.toString().buffer())
    }
}