package com.mob.lee.fastair.io.state

import androidx.annotation.IntDef

//连接成功
const val STATE_CONNECTED=1
//开始读取
const val STATE_READ_START=2
//开始写入
const val STATE_WRITE_START=3
//读取完成
const val STATE_READ_FINISH=4
//写入完成
const val STATE_WRITE_FINISH=5
//读取失败
const val STATE_READ_FAILD=6
//写入失败
const val STATE_WRITE_FAILD=7
//连接关闭
const val STATE_DISCONNECTED=8

@IntDef(STATE_CONNECTED,
        STATE_READ_START,
        STATE_WRITE_START,
        STATE_READ_FINISH,
        STATE_WRITE_FINISH,
        STATE_READ_FAILD,
        STATE_WRITE_FAILD,
        STATE_DISCONNECTED)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class SocketState{
    companion object{

    }
}

typealias SocketStateListener =(state:Int,info:String?)->Unit