package com.mob.lee.fastair.io

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce

/**
 * Created by Andy on 2018/1/16.
 */
class StringSender(val raw:String): SendTask {

    override fun exe(): ReceiveChannel<ProtocolBytes> {
        return produce {
            while (true) {
                send(ProtocolBytes.string(raw))
            }
        }
    }
}