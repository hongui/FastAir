package com.mob.lee.fastair.io.http

import androidx.annotation.IntDef

private const val _GET="get"
private const val _PUT="put"
const val GET=1
const val PUT=2
@IntDef(value=[GET,PUT])
annotation class Method

fun httpMethod(@Method method:Int)=when(method){
    GET-> _GET
    PUT-> _PUT
    else->""
}

fun httpMethod(method: String)=when(method.lowercase()){
    _GET-> GET
    _PUT-> PUT
    else->-1
}