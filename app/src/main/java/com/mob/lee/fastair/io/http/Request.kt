package com.mob.lee.fastair.io.http

data class Request(@Method val method:Int,val url:String,val contentLength:Long=0,val contentType:String="") {

}