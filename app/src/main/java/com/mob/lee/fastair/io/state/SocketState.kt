package com.mob.lee.fastair.io.state

import androidx.annotation.IntDef

@IntDef(SocketState.STATE_CONNECTED,
    SocketState.STATE_READ_START,
    SocketState.STATE_WRITE_START,
    SocketState.STATE_READ_FINISH,
    SocketState.STATE_WRITE_FINISH,
    SocketState.STATE_READ_FAILED,
    SocketState.STATE_WRITE_FAILED,
    SocketState.STATE_DISCONNECTED)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class SocketState{
    companion object{
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
        const val STATE_READ_FAILED=6
        //写入失败
        const val STATE_WRITE_FAILED=7
        //连接关闭
        const val STATE_DISCONNECTED=8
    }
}