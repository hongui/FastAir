package com.mob.lee.fastair.io.socket

import kotlinx.coroutines.flow.Flow
import java.nio.ByteBuffer

typealias Writer = Flow<ByteBuffer>