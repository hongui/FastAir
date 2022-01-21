package com.mob.lee.fastair.io.socket

import java.nio.channels.SocketChannel

typealias Writer =suspend (channel:SocketChannel)->Unit