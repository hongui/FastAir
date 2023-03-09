package com.mob.lee.fastair.io.state

import com.mob.lee.fastair.model.Record

sealed class State

class MessageState(val msg: String, val date: Long) : State()
sealed class RecordState(val record: Record) : State()

class StartRecordState(record: Record) : RecordState(record) {
    override fun toString(): String {
        return "StartState(record=$record)"
    }
}

class ProcessState(val progress: Long, val startDate: Long, record: Record) : RecordState(record) {
    fun percentage(): Float = progress.toFloat() / record.size

    fun speed(): Float {
        val duration = System.currentTimeMillis() - startDate
        return if (0L == duration) {
            0F
        } else {
            progress / 1024F / duration / 1000F
        }
    }

    override fun toString(): String {
        return "ProcessState(process=$progress, total=${record.size},record=$record)"
    }


}

class SuccessState(record: Record, val fromRemote: Boolean = false,val speed:Float=0F) : RecordState(record) {
    override fun toString(): String {
        val record = record as Record?
        return "SuccessState(duration=${record?.duration},record=$record)"
    }
}

class FailedState(record: Record?, val exception: Exception? = null) : RecordState(record ?: Record(0, 0, 0, "")) {
    override fun toString(): String {
        return "FailedState(record=$record,exception=$exception)"
    }
}