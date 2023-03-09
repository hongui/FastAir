package com.mob.lee.fastair.io

import com.mob.lee.fastair.io.state.*
import com.mob.lee.fastair.model.Record
import java.io.FileInputStream

class FileWriter(val record: Record, val listener: ProgressListener? = null) : Writer {
    var state = 0
    var alreadyFinished = 0L
    var tempLength = 0
    var startTime: Long = 0

    val size = 1024 * 8

    val bytes: ByteArray by lazy {
        ByteArray(size)
    }

    val stream: FileInputStream by lazy {
        FileInputStream(record.path)
    }


    override fun hasNext(): Boolean {
        return state != -1
    }

    override fun next(): ProtocolByte =
        when (state) {
            0 -> {
                state = 1
                ProtocolByte.empty()
            }

            1 -> {
                state = 2
                ProtocolByte.long(record.size)
            }

            2 -> {
                state = 3
                startTime = System.currentTimeMillis()
                record.state = Record.STATE_TRANSPORT
                listener?.invoke(StartRecordState(record))
                ProtocolByte.string(record.name)
            }

            else -> {
                try {
                    tempLength = stream.read(bytes)

                    if (-1 == tempLength || 0L == record.size) {
                        record.state = Record.STATE_SUCCESS
                        finished(SuccessState(record, speed = record.size.toFloat()/(System.currentTimeMillis()-startTime)/1000F))
                    } else {
                        alreadyFinished += tempLength
                        record.duration = System.currentTimeMillis() - startTime
                        listener?.invoke(ProcessState(alreadyFinished, startTime,record))
                        ProtocolByte.bytes(bytes.sliceArray(0 until tempLength))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    record.state = Record.STATE_FAILED
                    listener?.invoke(FailedState(record, e))
                    finished(FailedState(record))
                }
            }
        }

    fun finished(s: State): ProtocolByte {
        state = -1
        try {
            listener?.invoke(s)
            stream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ProtocolByte.empty()
    }
}