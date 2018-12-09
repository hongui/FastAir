package com.mob.lee.fastair.io

import android.util.Log

typealias Reader=(ProtocolByte) -> Unit

fun Reader.onError(msg:String?){
    Log.e("Reader",msg?:"未知错误")
}