package com.mob.lee.fastair.io

import com.mob.lee.fastair.io.state.FaildState
import com.mob.lee.fastair.io.state.ProcessState
import com.mob.lee.fastair.io.state.StartState
import com.mob.lee.fastair.io.state.State
import com.mob.lee.fastair.io.state.SuccessState
import java.io.File
import java.io.FileInputStream

class FileWriter(val file: File, val listener: ProcessListener? = null) : Writer {
    var state = 0
    var alreadyFinished = 0L
    var tempLength = 0

    val size = 1024 * 8

    val bytes: ByteArray by lazy {
        ByteArray(size)
    }

    val stream: FileInputStream by lazy {
        FileInputStream(file)
    }

    constructor(path: String) : this(File(path))

    constructor(path: String?, listener: ProcessListener? = null) : this(File(path), listener)

    override fun hasNext(): Boolean {
        //不发送文件夹
        if (null == file || file.isDirectory) {
            return false
        }
        return state != -1
    }

    override fun next(): ProtocolByte =
            when (state) {
                0 -> {
                    state = 1
                    listener?.invoke(StartState(obj = file))
                    ProtocolByte.empty()
                }

                1 -> {
                    state = 2
                    ProtocolByte.string(file.name)
                }

                2 -> {
                    state = 3
                    ProtocolByte.long(file.length())
                }

                else -> {
                    try {
                        tempLength = stream.read(bytes)

                        if (-1 == tempLength || 0L == file.length()) {
                            finished(SuccessState(obj = file))
                        } else {
                            alreadyFinished += tempLength
                            listener?.invoke(ProcessState(alreadyFinished, file.length(), obj = file))
                            ProtocolByte.bytes(bytes.sliceArray(0 until tempLength))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        listener?.invoke(FaildState(obj = file))
                        finished(FaildState(obj = file))
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