package com.mob.lee.fastair.io

import com.mob.lee.fastair.io.state.ProcessState
import com.mob.lee.fastair.io.state.StartState
import com.mob.lee.fastair.io.state.SuccessState
import java.io.File
import java.io.FileInputStream

class FileWriter(val file: File, val listener: ProcessListener? = null) : Writer() {
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

    constructor(path: String,listener: ProcessListener? = null) : this(File(path),listener)

    override fun hasNext(): Boolean = state != -1

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
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (-1 == tempLength) {
                        state = -1
                        try {
                            stream.close()
                            listener?.invoke(SuccessState(obj = file))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        ProtocolByte.empty()
                    } else {
                        alreadyFinished += tempLength
                        val total=if(0L==file.length()){
                            //文件长度为0，导致除0错误
                            1
                        }else{
                            file.length()
                        }
                        listener?.invoke(ProcessState(alreadyFinished, total, obj = file))
                        ProtocolByte.bytes(bytes.sliceArray(0 until tempLength))
                    }
                }
            }
}