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

class StartState(obj: Any? = null) : State(START, obj){
    override fun toString(): String {
        return "StartState(obj=${obj.toString()})"
    }
}

class ProcessState(val process: Long, val total: Long, obj: Any? = null) : State(PROCESS, obj) {
    fun percentage(): Float = (process.toFloat() / total * 100)
    override fun toString(): String {
        return "ProcessState(process=$process, total=$total,obj=${obj.toString()})"
    }


}

class SuccessState(val duration:Long=0L,obj: Any? = null) : State(SUCCESS, obj){
    override fun toString(): String {
        return "SuccessState(duration=$duration,obj=${obj.toString()})"
    }
}

class FaildState(obj: Any? = null) : State(FAILD, obj){
    override fun toString(): String {
        return "FaildState(obj=${obj.toString()})"
    }
}

fun parseFile(state: State): Record? {
    val file = state.obj as? File
    file ?: return null
    return Record(file.lastModified(), file.length(), file.lastModified(), file.absolutePath, state.what, 0)
}

const val START=0
const val PROCESS=1
const val SUCCESS=2
const val FAILD=4