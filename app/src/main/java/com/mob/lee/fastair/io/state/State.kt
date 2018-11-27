package com.mob.lee.fastair.io.state

import android.os.Handler
import android.os.Message
import com.mob.lee.fastair.model.Record
import java.io.File

sealed class State(val what: Int, val obj: Any? = null) {
    /*发送消息*/
    fun sendMessage(h: Handler?) {
        val msg = Message.obtain(h, what)
        msg.obj = this
        h?.sendMessage(msg)
    }
}

class StartState(obj: Any? = null) : State(START, obj)

class ProcessState(val process: Long, val total: Long, obj: Any? = null) : State(PROCESS, obj) {
    fun percentage(): Float = (process / total * 100).toFloat()
}

class SuccessState(obj: Any? = null) : State(SUCCESS, obj)

class FaildState(obj: Any? = null) : State(FAILD, obj)

fun parseFile(state: State): Record? {
    val file = state?.obj as? File
    file ?: return null
    return Record(file.lastModified(), file.length(), file.lastModified(), file.absolutePath, state.what, 0)
}

const val START=0
const val PROCESS=1
const val SUCCESS=2
const val FAILD=4