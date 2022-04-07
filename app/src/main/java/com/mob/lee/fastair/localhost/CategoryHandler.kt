package com.mob.lee.fastair.localhost

import android.content.Context
import com.mob.lee.fastair.io.http.*
import com.mob.lee.fastair.io.socket.Writer
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.repository.StorageDataSource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import org.json.JSONArray
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class CategoryHandler(val context: Context?):Handler {
    var category=-1
    val datasource by lazy {
        StorageDataSource()
    }
    override fun canHandleIt(request: Request): Boolean {
        val re=Regex("/category/(\\d)")
        val mat=re.find(request.url)
        mat?.let { category=it.groupValues[1].toInt() }
        return null!=mat
    }

    override suspend fun handle(request: Request, channel: SocketChannel): Writer {
        val categories = datasource.fetch(context, category)
        val all=categories.toList()
        val array = JSONArray()
        all.forEach { array.put(it.dump()) }
        return JsonResponse.json(array)
    }

    fun Record.dump():JSONObject{
        val json=JSONObject()
        json.put("name",this.name)
        json.put("id",this.id)
        json.put("path",this.path)
        json.put("size",this.size)
        json.put("date",this.date)
        return json
    }
}