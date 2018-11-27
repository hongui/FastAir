package com.mob.lee.fastair.io

import android.content.Context
import com.mob.lee.fastair.io.state.FaildState
import com.mob.lee.fastair.io.state.ProcessState
import com.mob.lee.fastair.io.state.StartState
import com.mob.lee.fastair.io.state.SuccessState
import com.mob.lee.fastair.utils.createFile
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class FileReader(val context: Context?, val listener: ProcessListener? = null) : Reader {
    var length = 0L
    var alreadyFinished = 0L
    lateinit var file: File
    lateinit var stream: FileOutputStream
    override fun invoke(data: ProtocolByte) {
        when (data.type) {
            ProtocolType.E -> {
                try {
                    stream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                listener?.invoke(SuccessState(obj = file))
            }

            ProtocolType.L -> {
                length = data.getLong()
            }

            ProtocolType.S -> {
                val name = data.getString()
                try {
                    val f = context?.createFile(name)
                    f?.let {
                        file = it
                        stream = file.outputStream()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    listener?.invoke(FaildState(obj = file))
                }
                listener?.invoke(StartState(obj = file))
            }

            else -> {
                stream.write(data.bytes())
                alreadyFinished += data.bytes().size
                listener?.invoke(ProcessState(alreadyFinished, length, obj = file))
            }
        }
    }
}