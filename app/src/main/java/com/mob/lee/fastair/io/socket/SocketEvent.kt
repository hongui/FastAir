package com.mob.lee.fastair.io.socket

import androidx.annotation.IntDef

const val READY=1
const val START=2
const val STOP=3
@IntDef(value = intArrayOf(READY, START, STOP))
annotation class SocketEvent {
}