package com.mob.lee.fastair.io.http

import com.mob.lee.fastair.utils.buffer
import org.json.JSONArray
import org.json.JSONObject
import java.nio.channels.SocketChannel

class JsonResponse(action:()->JSONObject,status:Int):Response<JSONObject>(action,status,JSON) {

    companion object{
        fun json(data: JSONArray?, status: Int= SUCCESS, code:Int=0, message:String="Success"):JsonResponse {
            val obj=JSONObject()
            obj.put("code",code)
            obj.put("msg", message)
            obj.put("data",data)
            return JsonResponse({
                obj
            },status)
        }
    }

    override fun onWriteBody(channel: SocketChannel, source: JSONObject) {
        val buffer=source.toString().buffer()
        channel.write(buffer)
    }

    override fun addHeader(source: JSONObject) {
        header.put("Content-Length",source.toString().buffer().remaining().toString())
    }
}