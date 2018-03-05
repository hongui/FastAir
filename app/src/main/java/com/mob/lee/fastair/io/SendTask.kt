package com.mob.lee.fastair.io

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.SendChannel

/**
 * Created by Andy on 2018/1/5.
 */
interface SendTask{
    fun exe():ReceiveChannel<ProtocolBytes>
}