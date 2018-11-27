package com.mob.lee.fastair.io

import com.mob.lee.fastair.io.state.SuccessState

class StringReader(val listener: ProcessListener? = null):Reader{
    override fun invoke(data: ProtocolByte) {
        listener?.invoke(SuccessState(obj = data.getString()))
    }
}