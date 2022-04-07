package com.mob.lee.fastair.io.http

import java.nio.ByteBuffer
import java.util.regex.Pattern

class Parser {
    companion object {
        fun parseRequest(buffer: ByteBuffer):Request? {
            var endHeader = false
            var index = 0
            val request=Request()
            do {
                val (value, position) = readLineWithUpdate(buffer, index)
                value?.let {
                    if(0==index){
                        val statusLine = parseStatusLine(it)
                        statusLine?:return null
                        request.method=statusLine.first
                        request.url=statusLine.second.first
                        statusLine.second.second?.let {
                            request.urlParams=it
                        }
                    }else {
                        request.header.add(it)
                    }
                }
                index = position
                endHeader = value?.isBlank() ?: true
            } while (endHeader)
            return request
        }

        fun parsePart(buffer: ByteBuffer):String?{
            val (bound,position) = readLineWithUpdate(buffer,buffer.position())
            bound?:return null
            val disposition = readLineWithUpdate(buffer, position)
            disposition.first?:return null
            disposition.first
            val contentType = readLineWithUpdate(buffer, position)

        }
        fun readLine(buffer: ByteBuffer, start: Int): Pair<String?, Int> {
            val limit = buffer.limit()
            if (start >= limit) return null to start
            for (i in start until limit) {
                val c = buffer.get(i)
                if (0 == c.compareTo('\n'.code)) {
                    return String(buffer.array(), start, i - start) to i
                }
            }
            return null to start
        }

        fun readLineWithUpdate(buffer: ByteBuffer, start: Int): Pair<String?, Int> {
            val value = readLine(buffer, start)
            value.first?.let { buffer.position(value.second) }
            return value
        }

        fun parseStatusLine(line: String): Triple<Int,Pair<String,Map<String,String>?>,String>?{
            val result=line.split(Regex("\\s+"))
            if (result.size == 3) {
                val method= httpMethod(result[0])
                val urlParameter = parseUrlParameter(result[1])
                val version = result[2]
                return Triple(method,urlParameter,version)
            }
            return null
        }

        fun parseUrlParameter(url:String):Pair<String,Map<String,String>?>{
            val urls = url.split("?")
            if (urls.size == 2) {
                val params = urls[1].split("&")
                if(params.size>1){
                    parseNameValue(params[0])?.let {
                        return urls[0] to mapOf(it)
                    }
                }else{
                    val maps = HashMap<String, String>()
                    for (p in params){
                        parseNameValue(p)?.let {
                            maps.put(it.first,it.second)
                        }
                    }
                    return urls[0] to maps
                }
            }
            return url to null
        }

        fun parseNameValue(value:String,sep:String="="):Pair<String,String>?{
            val result = value.split(Regex(sep), 2)
            if (result.size == 2) {
                return result[0].trim() to result[1].trim()
            }
            return null
        }
    }
}