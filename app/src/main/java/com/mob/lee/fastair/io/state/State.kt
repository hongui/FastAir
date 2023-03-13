package com.mob.lee.fastair.io.state

import com.mob.lee.fastair.model.Record
import kotlin.math.roundToInt

sealed class State

class MessageState(val msg: String, val date: Long) : State()
sealed class RecordState(val record: Record) : State()

class StartRecordState(record: Record) : RecordState(record) {
    override fun toString(): String {
        return "StartState(record=$record)"
    }
}

class TransmitState(val alreadyTransmited: Long, val duration: Long, record: Record) : RecordState(record) {
    fun percentage(): Float = alreadyTransmited.toFloat() / record.size

    override fun toString(): String {
        return "TransmitState(alreadyTransmited=$alreadyTransmited, total=${record.size},record=$record)"
    }

}

class SuccessState(record: Record, val fromRemote: Boolean = false,val speed:Float=0F) : RecordState(record) {
    override fun toString(): String {
        val record = record as Record?
        return "SuccessState(speed=${record?.duration},record=$record)"
    }

    fun needShowSpeed()=fromRemote||speed>0F
}

class FailedState(record: Record?, val exception: Exception? = null) : RecordState(record ?: Record(0, 0, 0, "")) {
    override fun toString(): String {
        return "FailedState(record=$record,exception=$exception)"
    }
}

fun speed(bytes:Long,duration:Long):Float=(if(0L==duration) 0F else (bytes.toFloat()/1024F/1024F/(duration.toFloat()/1000F))*100F).roundToInt()/100F