package com.mob.lee.fastair.io

import java.nio.ByteBuffer

/**
 * Created by Andy on 2017/12/31.
 * bytes包含两个部分：头部和内容，共占五个字节
 * 头部：第一个字节是类型，取值为ProtocolType
 *      后四个字节为内容长度
 * 内容，真正的有效数据
 */
data class ProtocolByte(val bytes: ByteBuffer) {
    companion object {
        /*类型长度*/
        @JvmStatic
        val TYPE_LENGTH = 1

        /*内容长度*/
        @JvmStatic
        val CONTENT_LENGTH = 4

        /*头部长度*/
        @JvmStatic
        val HEAD_LENGTH = TYPE_LENGTH + CONTENT_LENGTH

        fun wrap(bytes: ByteArray, type: Byte): ProtocolByte {
            val buffer = ByteBuffer.allocate(size(bytes))
            /*类型*/
            buffer.put(type)
            /*内容长度*/
            buffer.putInt(bytes.size)
            /*内容*/
            buffer.put(bytes)
            buffer.flip()
            return ProtocolByte(buffer)
        }

        fun empty(): ProtocolByte {
            val buffer = ByteBuffer.allocate(HEAD_LENGTH)
            buffer.put(ProtocolType.E)
            buffer.putInt(0)
            buffer.flip()
            return ProtocolByte(buffer)
        }

        fun bytes(bytes: ByteArray) = wrap(bytes, ProtocolType.B)

        fun long(value: Long): ProtocolByte {
            val buffer = ByteBuffer.allocate(8 + HEAD_LENGTH)
            buffer.put(ProtocolType.L)
            buffer.putInt(8)
            buffer.putLong(value)
            buffer.flip()
            return ProtocolByte(buffer)
        }

        fun string(value: String): ProtocolByte {
            val temp = value.toByteArray()
            val buffer = ByteBuffer.allocate(size(temp))
            buffer.put(ProtocolType.W)
            buffer.putInt(temp.size)
            buffer.put(temp)
            buffer.flip()
            return ProtocolByte(buffer)
        }

        fun size(bytes: ByteArray) = bytes.size + HEAD_LENGTH
    }

    fun getContentLength(): Int {
        return bytes.getInt(1)
    }

    fun getType(): Byte {
        return bytes.get(0)
    }

    fun getLong(): Long {
        return bytes.getLong(HEAD_LENGTH)
    }

    fun getString(): String {
        val byteArray=bytes()
        return String(byteArray.array(), HEAD_LENGTH,byteArray.remaining())
    }

    fun bytes(): ByteBuffer {
        bytes.position(HEAD_LENGTH)
        return bytes.slice()
    }
}