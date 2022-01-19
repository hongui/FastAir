package com.mob.lee.fastair.utils

import java.nio.ByteBuffer

fun String.buffer()=ByteBuffer.wrap(this.encodeToByteArray())