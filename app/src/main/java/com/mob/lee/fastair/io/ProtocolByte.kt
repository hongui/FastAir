package com.mob.lee.fastair.io

import java.nio.ByteBuffer

/**
 * Created by Andy on 2017/12/31.
 * bytes包含两个部分：头部和内容，共占五个字节
 * 头部：第一个字节是类型，取值为ProtocolType
 *      后四个字节为内容长度
 * 内容，真正的有效数据
 */
data class ProtocolByte(val bytes:ByteBuffer, val type: ProtocolType){

    companion object {
        /*类型长度*/
        const val TYPE_LENGTH=1
        /*内容长度*/
        const val CONTENT_LENGTH=4
        /*头部长度*/
        const val HEAD_LENGTH= TYPE_LENGTH+ CONTENT_LENGTH

        fun wrap(bytes: ByteArray,type: ProtocolType):ProtocolByte{
            val buffer = ByteBuffer.allocate(size(bytes))
            /*类型*/
            buffer.put(type.ordinal.toByte())
            /*内容长度*/
            buffer.putInt(bytes.size)
            /*内容*/
            buffer.put(bytes)
            buffer.flip()
            return ProtocolByte(buffer,type)
        }

        fun empty():ProtocolByte{
            val buffer=ByteBuffer.allocate(TYPE_LENGTH)
            buffer.put(ProtocolType.CE)
            buffer.flip()
            return ProtocolByte(buffer,ProtocolType.L)
        }

        fun bytes(bytes: ByteArray)= wrap(bytes,ProtocolType.B)

        fun long(value:Long):ProtocolByte{
            val buffer=ByteBuffer.allocate(8+ HEAD_LENGTH)
            buffer.put(ProtocolType.CL)
            buffer.putInt(8)
            buffer.putLong(value)
            buffer.flip()
            return ProtocolByte(buffer,ProtocolType.L)
        }

        fun string(value:String):ProtocolByte{
            val temp=value.toByteArray()
            val buffer=ByteBuffer.allocate(size(temp))
            buffer.put(ProtocolType.CC)
            buffer.putInt(temp.size)
            buffer.put(temp)
            buffer.flip()
            return ProtocolByte(buffer,ProtocolType.C)
        }

        fun size(bytes: ByteArray)=bytes.size+ HEAD_LENGTH
    }

    fun getContentSize():Int{
        bytes.position(TYPE_LENGTH)
        return bytes.int
    }

    fun getLong():Long{
        bytes.position(HEAD_LENGTH)
        return bytes.long
    }

    fun getString():String{
        return String(bytes())
    }

    fun bytes():ByteArray{
        val content=ByteBuffer.wrap(bytes.array(), HEAD_LENGTH,bytes.limit()- HEAD_LENGTH)
        return content.array()
    }
}