package com.mob.lee.fastair.io.http

import androidx.annotation.IntDef

private const val _GET="get"
private const val _PUT="put"
private const val _POST="post"
const val GET=1
const val POST=2
const val PUT=3
@IntDef(value=[GET, POST,PUT,])
annotation class Method

fun httpMethod(@Method method:Int)=when(method){
    GET-> _GET
    POST-> _POST
    PUT-> _PUT
    else->""
}

fun httpMethod(method: String)=when(method.trim().lowercase()){
    _GET-> GET
    _POST-> POST
    _PUT-> PUT
    else->-1
}