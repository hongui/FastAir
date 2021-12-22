package com.mob.lee.fastair.io

class ResponseCode {
    fun statusLine(code:Int,status:String)="http/1.1 ${code} ${status}"
    fun resposeHeadLine(key:String,value:String)="${key}: ${value}\n"
    fun success()=statusLine(200,"OK")
}