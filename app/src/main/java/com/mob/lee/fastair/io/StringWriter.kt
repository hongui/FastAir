package com.mob.lee.fastair.io

class StringWriter(val value: String) : Writer() {
    var writed = true
    override fun hasNext() = writed

    override fun next(): ProtocolByte = ProtocolByte.string(value).apply { writed = false }
}