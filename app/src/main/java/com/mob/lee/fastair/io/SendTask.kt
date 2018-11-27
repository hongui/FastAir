package com.mob.lee.fastair.io

import kotlinx.coroutines.channels.ReceiveChannel

/**
 * Created by Andy on 2018/1/5.
 */
interface SendTask{
    fun exe(): ReceiveChannel<ProtocolByte>
}