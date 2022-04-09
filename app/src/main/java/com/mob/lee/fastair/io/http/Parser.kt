package com.mob.lee.fastair.io.http

import android.net.Uri
import java.nio.ByteBuffer

class Parser {
    companion object {
        const val CONTENT_DISPOSITION="Content-Disposition"
        const val CONTENT_TYPE="Content-Type"
        const val CONTENT_DISPOSITION_LEN= CONTENT_DISPOSITION.length
        const val CONTENT_TYPE_LEN= CONTENT_TYPE.length

        fun parseRequest(buffer: ByteBuffer): Request? {

            val lines = parseArea(buffer)
            if (lines.isEmpty()) {
                return null
            }

            val statusLine = parseStatusLine(lines[0])

            statusLine?:return null

            val headers = HashMap<String, String>()
            if (lines.size > 1) {
                lines.map { parseNameValue(it, ":") }
                    .forEach {
                        it?.let {
                            headers.put(it.first, it.second)
                        }
                    }
            }

            return Request(statusLine.first, statusLine.second.first, statusLine.second.second ?: emptyMap(), headers)
        }

        fun parsePart(buffer: ByteBuffer): Pair<String,String>? {
            val lines = parseArea(buffer)
            if (lines.isEmpty()) {
                return null
            }
            val disposition=lines.find { it.contains(CONTENT_DISPOSITION) }?.substring(CONTENT_DISPOSITION_LEN+1)?.trim()
            val contentType=lines.find { it.contains(CONTENT_TYPE) }?.substring(CONTENT_TYPE_LEN+1)?.trim()
            disposition?:return null
            return disposition to (contentType?:TEXT)
        }

        fun parseArea(buffer: ByteBuffer): List<String> {
            var isEnd = false
            var index = buffer.position()
            val list = ArrayList<String>()
            do {
                val (value, position) = readLineWithUpdate(buffer, index)
                value?.let {
                    list.add(it)
                } ?: let {
                    isEnd = true
                }
                index = position
                isEnd = value?.isBlank() ?: true
            } while (!isEnd)
            return list
        }

        fun readLine(buffer: ByteBuffer, start: Int=buffer.position()): Pair<String?, Int> {
            val limit = buffer.limit()
            if (start >= limit) return null to start
            for (i in start until limit) {
                val c = buffer.get(i)
                if (0 == c.compareTo('\n'.code)) {
                    return String(buffer.array(), start, i - start) to i+1
                }
            }
            return null to start
        }

        fun readLineWithUpdate(buffer: ByteBuffer, start: Int=buffer.position()): Pair<String?, Int> {
            val value = readLine(buffer, start)
            value.first?.let { buffer.position(value.second) }
            return value
        }

        fun parseStatusLine(line: String): Triple<Int, Pair<String, Map<String, String>?>, String>? {
            val result = line.split(Regex("\\s+"))
            if (result.size >= 3) {
                val method = httpMethod(result[0])
                val urlParameter = parseUrlParameter(result[1])
                val version = result[2]
                return Triple(method, urlParameter, version)
            }
            return null
        }

        fun parseUrlParameter(url: String): Pair<String, Map<String, String>?> {
            val urls = url.split("?")
            if (urls.size == 2) {
                val strs = urls[1].split("&")
                val maps = if (strs.size > 1) {
                    parseNameValue(strs[0])?.let {
                        mapOf(it)
                    }
                } else {
                    val params = HashMap<String, String>()
                    for (p in strs) {
                        parseNameValue(p)?.let {
                            params.put(it.first, it.second)
                        }
                    }
                    params
                }
                return Uri.decode(urls[0]) to maps
            }
            return url to null
        }

        fun parseNameValue(value: String, sep: String = "="): Pair<String, String>? {
            val result = value.split(Regex(sep), 2)
            if (result.size == 2) {
                return result[0].trim() to result[1].trim()
            }
            return null
        }
    }
}