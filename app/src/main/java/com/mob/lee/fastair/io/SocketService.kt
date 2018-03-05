package com.mob.lee.fastair.io

import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel


/**
 * Created by Andy on 2017/11/29.
 */
class SocketService(var keepAlive:Boolean=false) {
    val readers = ArrayList<(bytes: ProtocolBytes) -> Unit>()
    private val writers = ArrayList<SendTask>()
    private var isOpen: Boolean = false

    fun open(port: Int, host: String? = null) = runBlocking {
        if (isOpen) {
            return@runBlocking
        }
        isOpen = true
        try {
            val channel = if (null != host) {
                val channel = SocketChannel.open()
                channel.connect(InetSocketAddress(host, port))
                channel
            } else {
                val server = ServerSocketChannel.open()
                server.socket().bind(InetSocketAddress(port))
                server.accept()
            }
            handleRead(channel)
            handleWrite(channel)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun read(reader: (bytes: ProtocolBytes) -> Unit) {
        readers.add(reader)
    }

    fun write(task: SendTask) {
        writers.add(task)
    }

    fun close() {
        isOpen=false
        keepAlive=false
    }

    private fun handleRead(channel: SocketChannel) {
        launch {
            while (isOpen||keepAlive) {
                try {
                    val head= readFix(5,channel)
                    val type = head.get()
                    val length = head.int
                    val bytes= readFix(length,channel)
                    for (reader in readers) {
                        reader(ProtocolBytes.wrap(bytes.array(), type))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            channel.socket().shutdownInput()
        }
    }

    private fun handleWrite(channel: SocketChannel) {
        launch {
            while (isOpen||keepAlive) {
                if (writers.isEmpty()){
                    continue
                }
                val bytes = writers.elementAt(0)
                try {
                    val tasks = bytes.exe()
                    for (task in tasks) {
                        channel.write(task.bytes)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    writers.removeAt(0)
                }
            }
            channel.socket().shutdownOutput()
        }
    }

    private fun readFix(size:Int, channel: SocketChannel):ByteBuffer{
        val buffer=ByteBuffer.allocate(size)
        var tempSize=-1
        while (-1==tempSize){
            buffer.clear()
            tempSize=channel.read(buffer)
        }
        while (tempSize<size){
            buffer.limit(size)
            buffer.position(tempSize)
            val tempBuffer=ByteBuffer.allocate(size-tempSize)
            var innerSize=-1
            while (-1==innerSize){
                tempBuffer.clear()
                innerSize=channel.read(tempBuffer)
            }
            tempSize+=innerSize
            for(i in 0 until innerSize){
                buffer.put(tempBuffer.get(i))
            }
        }
        buffer.flip()
        return buffer
    }
}