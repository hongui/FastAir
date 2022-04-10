package com.mob.lee.fastair.io.http

import android.net.Uri
import android.util.Log
import java.nio.ByteBuffer

class Parser {
    companion object {
        const val CONTENT_DISPOSITION = "Content-Disposition"
        const val CONTENT_TYPE = "Content-Type"
        const val CONTENT_DISPOSITION_LEN = CONTENT_DISPOSITION.length
        const val CONTENT_TYPE_LEN = CONTENT_TYPE.length

        fun parseRequest(buffer: ByteBuffer): Request? {

            val (line, _) = readLineString(buffer)
            line?:return null

            val statusLine = parseStatusLine(line) ?: return null

            val area = readAreaString(buffer) ?: return null

            val headers = HashMap<String, String>()

            area
                .lines()
                .map { parseNameValue(it, ":") }
                .forEach {
                    it?.let {
                        headers.put(it.first, it.second)
                    }
                }

            return Request(statusLine.first, statusLine.second.first, statusLine.second.second ?: emptyMap(), headers)
        }

        fun parsePart(buffer: ByteBuffer): Triple<String,String, String>? {
            val b = readAreaString(buffer) ?: return null

            val lines = b.lines()
            val disposition = lines.find { it.contains(CONTENT_DISPOSITION) }?.substring(CONTENT_DISPOSITION_LEN + 1)?.trim()
            val contentType = lines.find { it.contains(CONTENT_TYPE) }?.substring(CONTENT_TYPE_LEN + 1)?.trim()
            disposition ?: return null
            return Triple(lines[0],disposition , (contentType ?: TEXT))
        }

        fun readAreaString(buffer: ByteBuffer): String? {
            val area = readArea(buffer)
            if (!area.hasRemaining()) {
                return null
            }
            return String(area.array(), 0, area.limit())
        }

        fun readArea(buffer: ByteBuffer): ByteBuffer {
            var isEnd = false
            var position = buffer.position()
            var start = position
            do {
                val value = readLine(buffer)
                isEnd = value.first?.let {
                    Log.e("Tag", "==========${subStringArray(it)}====${it.limit()}")
                    it.remaining() == 2 &&
                            0 == it.get().compareTo('\r'.code) &&
                            0 == it.get().compareTo('\n'.code)
                } ?: true
                position = value.second
            } while (!isEnd)
            return buffer.duplicate().apply {
                position(start)
                limit(position)
            }
        }

        fun readLineString(buffer: ByteBuffer, start: Int = buffer.position(), updatePosition: Boolean = true): Pair<String?, Int> {
            return readLine(buffer, start, updatePosition).run {
                first?.let {
                    subStringArray(it) to second
                } ?: null to second
            }
        }

        fun readLine(buffer: ByteBuffer, start: Int = buffer.position(), updatePosition: Boolean = true): Pair<ByteBuffer?, Int> {
            val limit = buffer.limit()
            if (start >= limit) return null to start

            // We need recovery later
            if (!updatePosition) {
                buffer.mark()
            }

            //For slice
            buffer.position(start)

            var hasBefore = false
            for (i in start until limit) {
                val c = buffer.get(i)
                if (0 == c.compareTo('\n'.code) && hasBefore) {
                    // i is \n now,we skip it next time
                    val position = if (i == buffer.limit()) i else i + 1
                    val b = buffer.duplicate()
                    //It's ugly,there are not a sub buffer interface,callee need use careful
                    b.limit(position)
                    b.position(start)

                    if (updatePosition) {
                        buffer.position(position)
                    } else {
                        buffer.reset()
                    }
                    return b to position
                } else if (0 == c.compareTo('\r'.code)) {
                    hasBefore = true
                } else {
                    hasBefore = false
                }
            }
            return null to start
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

        private fun subStringArray(buffer: ByteBuffer) = String(buffer.array(), buffer.position(), buffer.limit() - buffer.position())
    }
}