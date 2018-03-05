package com.mob.lee.fastair.io

import java.nio.ByteBuffer

/**
 * Created by Andy on 2017/12/31.
 */
data class ProtocolBytes(val bytes:ByteBuffer,val type: Byte){

    companion object {
        fun wrap(bytes: ByteArray,type: Byte):ProtocolBytes{
            val buffer = ByteBuffer.allocate(size(bytes))
            buffer.put(type)
            buffer.putInt(bytes.size)
            buffer.put(bytes)
            buffer.flip()
            return ProtocolBytes(buffer,type)
        }

        fun bytes(bytes: ByteArray):ProtocolBytes{
            val buffer = ByteBuffer.allocate(size(bytes))
            val type='B'.toByte()
            buffer.put( type)
            buffer.putInt(bytes.size)
            buffer.put(bytes)
            buffer.flip()
            return ProtocolBytes(buffer,type)
        }

        fun long(value:Long):ProtocolBytes{
            val buffer=ByteBuffer.allocate(8+1+4)
            val type='L'.toByte()
            buffer.put(type)
            buffer.putInt(8)
            buffer.putLong( value)
            buffer.flip()
            return ProtocolBytes(buffer,type)
        }

        fun string(value:String):ProtocolBytes{
            val temp=value.toByteArray()
            val buffer=ByteBuffer.allocate(size(temp))
            val type='S'.toByte()
            buffer.put(type)
            buffer.putInt(temp.size)
            buffer.put(temp)
            buffer.flip()
            return ProtocolBytes(buffer,type)
        }

        fun size(bytes: ByteArray)=bytes.size+5
    }

    fun getLong():Long{
        wraped()
        return bytes.long
    }

    fun getString():String{
        return String(bytes())
    }

    fun bytes():ByteArray{
        val array=ByteArray(bytes.limit()-5)
        for(i in 5 until bytes.limit() ){
            array[i-5]=bytes.get(i)
        }
        return array
    }

    fun wraped(){
        bytes.position(5)
    }
}