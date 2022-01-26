package com.mob.lee.fastair.io.http

import java.net.URLDecoder
import java.nio.ByteBuffer

data class Request(@Method val method: Int, val url: String, val urlParams: Map<String, String>, val params: Map<String, String>) {

    fun urlParam(key:String)=urlParams.get(key)

    companion object {
        fun parse(buffer: ByteBuffer): Request? {
            val request = String(buffer.array(), 0, buffer.position())
            val sequences = request.lineSequence()
            val head = sequences.firstOrNull()?.let { parseStatusLine(it) }
            head ?: return null
            val urlParams = HashMap<String, String>()
            head.third.split("&").map {
                val value = it.split("=")
                if (value.size == 2) {
                    var param=""
                    try {
                        param=URLDecoder.decode(value[1],"UTF-8")
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                    if(param.isBlank()){
                        null
                    }else{
                        value[0] to param
                    }

                } else {
                    null
                }
            }.filter { null != it }.forEach { urlParams.put(it!!.first, it.second) }

            val params = HashMap<String, String>()
            sequences.forEachIndexed { index, s ->
                if (index > 0) {
                    val pair = s.split(":")
                    if (pair.size == 2) {
                        params.put(pair.first().trim(), pair.last().trim())
                    }
                }
            }
            var url=""
            try {
                url=URLDecoder.decode(head.second, "UTF-8")
            }catch (e:Exception){
                e.printStackTrace()
            }
            return Request(httpMethod(head.first), url, urlParams, params)
        }

        private fun parseStatusLine(line: String): Triple<String, String, String>? {
            val values = line.split(Regex("\\s"))
            if (values.size == 3) {
                var param = values[1].split("?")
                return Triple(values[0], param[0], if(1==param.size) "" else param[1])
            }
            return null
        }
    }
}