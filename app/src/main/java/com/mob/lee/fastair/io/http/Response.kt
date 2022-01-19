package com.mob.lee.fastair.io.http

class Response(val status:Int) {
    val values=HashMap<String,String>()

    init {
        values.put("content-type","text/html; charset=utf-8")
    }

    override fun toString(): String {
        return "${status} ${status(status)}\n${values.entries.joinToString("\n", transform = {"${it.key}: ${it.value}"})}\r\n"
    }

    companion object{
        fun success()=Response(SUCCESS)
        fun notfound()=Response(NOTFOUNT)
        fun error()=Response(SERVERERROR)
    }
}

private const val SUCCESS=200
private const val NOTFOUNT=404
private const val SERVERERROR=500
fun status(code:Int){
    when(code){
        SUCCESS->"ok"
        NOTFOUNT->"Not Found"
        else->"Internal Server Error"
    }
}