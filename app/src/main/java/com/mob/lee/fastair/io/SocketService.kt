package com.mob.lee.fastair.io

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.channels.*


/**
 * Created by Andy on 2017/11/29.
 */
class SocketService(var keepAlive: Boolean = false) {

    /**
     *发送消息
     * @see write
     */
    private val writers = Channel<Writer>(Channel.UNLIMITED)

    /**
     * 指示当前连接是否已经开启
     */
    var isConnected: Boolean = false

    /**
     * 通信通道
     */
    private var channel: SocketChannel? = null

    private var server: ServerSocketChannel? = null

    private val listeners = ArrayList<SocketStateListener>()

    /**
     * 建立连接，host为空作为客户端，否则作为服务器
     * @param port 端口号
     * @param host 主机地址，当该参数为null时，则开启一个socket监听
     **/
    suspend fun open(context: CoroutineScope, port: Int, host: String? = null,extra: Any?=null) {
        Log.i(TAG, "try to connect ${host}:${port}")
        if (isConnected) {
            return
        }
        try {
            if (null != host) {
                var time = 0
                delay(500)
                //等待10S
                while (false == (channel?.isConnected ?: false) && time < 20) {
                    try {
                        if (null == channel) {
                            channel = SocketChannel.open()
                            channel?.socket()?.reuseAddress = true
                        }
                        channel?.connect(InetSocketAddress(host, port))
                    } catch (e: AlreadyConnectedException) {
                        break
                    } catch (e: ConnectionPendingException) {
                        Log.e(TAG, "To Connect exception: ${e.message}")
                    } catch (e: AsynchronousCloseException) {
                        Log.e(TAG, "To Connect exception: ${e.message}")
                    } catch (e: UnresolvedAddressException) {
                        Log.e(TAG, "To Connect exception: ${e.message}")
                    }
                    if (false == channel?.isConnected) {
                        delay(500)
                        time++
                        Log.w(TAG, "Retry Connect ${time}")
                    }
                }
                Log.i(TAG, "Connect ${host} : ${port} success,times = $time")
            } else {
                server = ServerSocketChannel.open()
                server?.socket()?.reuseAddress = true
                server?.socket()?.bind(InetSocketAddress(port))
                channel = server?.accept()
                Log.i(TAG, "Accept connect ${port} success")
            }

            channel?.let { c ->
                Log.i(TAG, "Connect finished.")
                isConnected = true
                listeners.forEach { it.onConnected(c,extra) }

                context.launch(Dispatchers.IO) {
                    handleRead(c)
                }
                context.launch(Dispatchers.IO) {
                    handleWrite(c)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Connect exception: ${e.message}")

            close(e)
        }
    }

    /**
     * 添加写入数据的监听
     */
    suspend fun write(writer: Writer) {
        writers.send(writer)
    }

    /**
     * 关闭连接
     */
    fun close(e: Exception? = null) {
        listeners.forEach { it.onDisconnected(e) }
        if (!isConnected) {
            return
        }
        isConnected = false
        writers.close()
        try {
            server?.close()
            channel?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        listeners.clear()
    }

    /**
     * 读取数据
     *
     * 1、读取固定的5个字节，里面包含4个字节的内容长度，和1个字节的类型标识
     * 2、分解类型和长度
     * 3、读取真正内容
     * 4、组装成消息对象，通知每个监听
     *
     * @see ProtocolType
     * @see ProtocolByte
     */
    private suspend fun handleRead(channel: SocketChannel) {
        Log.i(TAG, "Start handle read.")
        while (isConnected || keepAlive) {
            try {
                listeners.forEach { it.onReadStart(channel) }
                val head = readFix(channel, 5)
                val type = head.get()
                val length = head.int
                val bytes = readFix(channel, length)
                listeners.forEach {
                    it.onReadFinished(channel, ProtocolByte.wrap(bytes.array(), type))
                }

            } catch (e: Exception) {
                Log.e(TAG, "Read exception: ${e.message}")
                listeners.forEach { it.onReadError(channel, e) }
                //读取失败
                if (e is BufferUnderflowException || e is AsynchronousCloseException) {
                    close(e)
                    break
                }
            }
        }
    }

    /**
     * 发送数据
     */
    private suspend fun handleWrite(channel: SocketChannel) {
        Log.i(TAG, "Start handle write.")
        var writer: Writer? = null
        while (isConnected || keepAlive) {
            try {
                writer = writers.receive()
                listeners.forEach { it.onWriteStart(channel, writer) }
                for (d in writer) {
                    val bytes = d.bytes
                    while (bytes.hasRemaining()) {
                        channel.write(bytes)
                    }
                }
                listeners.forEach { it.onWriteFinished(channel, writer) }
            } catch (e: Exception) {
                Log.e(TAG, "Write exception: ${e.message}")
                listeners.forEach { it.onWriteError(channel, e, writer) }
                if (e is ClosedReceiveChannelException) {
                    close(e)
                    break
                }
            }
        }
    }

    /**
     * 读取特定长度的字节，可能会阻塞
     */
    private suspend fun readFix(channel: SocketChannel, targetSize: Int): ByteBuffer {
        val buffer = ByteBuffer.allocate(targetSize)
        while (buffer.hasRemaining() && isConnected) {
            channel.read(buffer)
        }
        buffer.flip()
        return buffer
    }

    fun addListener(listener: SocketStateListener): SocketStateListener {
        listeners.add(listener)
        return listener
    }

    fun removeListener(listener: SocketStateListener?) {
        listeners.remove(listener)
    }

    fun isGroupOwner() = null != server

    companion object {
        const val TAG = "SocketService"
    }

    interface SocketStateListener {
        fun onConnected(channel: SocketChannel,extra:Any?)
        fun onDisconnected(exception: Exception?)
        fun onReadStart(channel: SocketChannel)
        fun onReadFinished(channel: SocketChannel, data: ProtocolByte)
        fun onReadError(channel: SocketChannel, exception: Exception)

        fun onWriteStart(channel: SocketChannel, writer: Writer)
        fun onWriteFinished(channel: SocketChannel, writer: Writer)

        fun onWriteError(channel: SocketChannel, exception: Exception, writer: Writer?)
    }
}