package com.mob.lee.fastair.io

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel


/**
 * Created by Andy on 2017/11/29.
 */
class SocketService(val scope: CoroutineScope, var keepAlive: Boolean = false) {
    private val readers = HashSet<Reader>()
    private val writers = Channel<Writer>()
    private var isOpen: Boolean = false
    private var channel: SocketChannel? = null

    /*建立连接，host为空作为客户端，否则作为服务器*/
    fun open(port: Int, host: String? = null) = scope.launch(Dispatchers.IO) {
        if (isOpen) {
            return@launch
        }
        isOpen = true
        try {
            if (null != host) {
                channel = SocketChannel.open()
                channel?.connect(InetSocketAddress(host, port))
            } else {
                val server = ServerSocketChannel.open()
                server.socket().bind(InetSocketAddress(port))
                channel = server.accept()
            }
            handleRead()
            handleWrite()
        } catch (e: Exception) {
            e.printStackTrace()
            channel?.let {
                it.close()
            }
        }
    }

    /*添加收到数据的监听*/
    fun read(reader: Reader) {
        readers.add(reader)
    }

    /*添加写入数据的监听*/
    fun write(writer: Writer) = scope.launch {
        writers.send(writer)
    }

    /*关闭连接*/
    fun close() {
        isOpen = false
        keepAlive = false
        writers.close()
    }

    /*读取数据*/
    private fun handleRead() = scope.launch(Dispatchers.IO) {
        while (isOpen || keepAlive) {
            try {
                val head = readFix(5)
                val type = head.get()
                val length = head.int
                val bytes = readFix(length)
                for (reader in readers) {
                    reader(ProtocolByte.wrap(bytes.array(), ProtocolType.wrap(type)))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                for (reader in readers) {
                    reader?.onError(e.message)
                }
            }
        }
        channel?.socket()?.shutdownInput()
    }

    /*发送数据*/
    private fun handleWrite()=scope.launch(Dispatchers.IO) {
        while (isOpen || keepAlive) {
            try {
                val data = writers.receive()
                for (d in data) {
                    val bytes = d.bytes
                    while (bytes.hasRemaining()) {
                        channel?.write(bytes)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        channel?.socket()?.shutdownOutput()
    }

    /*读取特定长度的字节，可能会阻塞*/
    private fun readFix(targetSize: Int): ByteBuffer {
        val buffer = ByteBuffer.allocate(targetSize)
        while (buffer.hasRemaining()) {
            channel?.read(buffer)
        }
        buffer.flip()
        return buffer
    }
}