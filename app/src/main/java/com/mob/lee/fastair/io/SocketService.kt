package com.mob.lee.fastair.io

import android.util.Log
import com.mob.lee.fastair.io.state.*
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
     * 读取监听器，消息到来会通知
     * @see read
     */
    private val readers = HashSet<Reader>()

    /**
     *发送消息
     * @see write
     */
    private val writers = Channel<Writer>(Channel.UNLIMITED)

    /**
     * 指示当前连接是否已经开启
     */
    private var isOpen: Boolean = false

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
    suspend fun open(context:CoroutineScope,port: Int, host: String? = null) {
        Log.d(TAG, "try to connect ${host}:${port}")
        if (isOpen) {
            return
        }
        try {
            if (null != host) {
                var time = 0
                delay(500)
                //等待10S
                while ((false == channel?.isConnected) && time < 20) {
                    try {
                        if(null==channel) {
                            channel = SocketChannel.open()
                            channel?.socket()?.reuseAddress = true
                        }
                        channel?.connect(InetSocketAddress(host, port))
                    } catch (e: AlreadyConnectedException) {
                        break
                    } catch (e: ConnectionPendingException) {
                        Log.d(TAG, "To Connect exception: ${e.message}")
                    }catch (e: AsynchronousCloseException) {
                        Log.d(TAG, "To Connect exception: ${e.message}")
                    } catch (e: UnresolvedAddressException) {
                        Log.d(TAG, "To Connect exception: ${e.message}")
                    }
                    if(false==channel?.isConnected) {
                        delay(500)
                        time++
                        Log.d(TAG, "Retry Connect ${time}")
                    }
                }
                Log.d(TAG, "Connect ${host} : ${port} success")
            } else {
                server = ServerSocketChannel.open()
                server?.socket()?.reuseAddress = true
                server?.socket()?.bind(InetSocketAddress(port))
                channel = server?.accept()
                Log.d(TAG, "Accept connect ${port} success")
            }

            isOpen = true
            updateState(STATE_CONNECTED)

            context.launch(Dispatchers.IO) {
                handleRead()
            }
            context.launch(Dispatchers.IO) {
                handleWrite()
            }
        } catch (e: Exception) {
            Log.d(TAG, "Connect exception: ${e.message}")

            close(e)
        }
    }

    /**
     * 添加收到数据的监听
     **/
    suspend fun read(reader: Reader) {
        readers.add(reader)
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
        if (!isOpen) {
            return
        }
        isOpen = false
        writers.close()
        for (r in readers) {
            r.onError(e?.message)
        }
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
    private suspend fun handleRead() {
        while (isOpen || keepAlive) {
            try {
                updateState(STATE_READ_START)
                val head = readFix(5)
                val type = head.get()
                val length = head.int
                val bytes = readFix(length)
                for (reader in readers) {
                    reader(ProtocolByte.wrap(bytes.array(), ProtocolType.wrap(type)))
                }
                updateState(STATE_READ_FINISH)
            } catch (e: Exception) {
                Log.d(TAG, "Read exception: ${e.message}")
                updateState(STATE_READ_FAILD, e.message)
                for (reader in readers) {
                    reader.onError(e.message)
                }
                //读取失败
                if (e is BufferUnderflowException || e is AsynchronousCloseException) {
                    updateState(STATE_DISCONNECTED)
                    close(e)
                    break
                }
            }
        }
    }

    /**
     * 发送数据
     */
    private suspend fun handleWrite() {
        while (isOpen || keepAlive) {
            try {
                val data = writers.receive()
                updateState(STATE_WRITE_START)
                for (d in data) {
                    val bytes = d.bytes
                    while (bytes.hasRemaining()) {
                        channel?.write(bytes)
                    }
                }
                updateState(STATE_WRITE_FINISH)
            } catch (e: Exception) {
                Log.d(TAG, "Write exception: ${e.message}")
                updateState(STATE_WRITE_FAILD, e.message)
                if (e is ClosedReceiveChannelException) {
                    close(e)
                    updateState(STATE_DISCONNECTED)
                    break
                }
            }
        }
    }

    /**
     * 读取特定长度的字节，可能会阻塞
     */
    private suspend fun readFix(targetSize: Int): ByteBuffer {
        val buffer = ByteBuffer.allocate(targetSize)
        while (buffer.hasRemaining() && isOpen) {
            channel?.read(buffer)
        }
        buffer.flip()
        return buffer
    }

    /**
     * 状态更新
     * @see SocketState
     */
    private fun updateState(@SocketState state: Int, info: String? = null) {
        for (l in listeners) {
            l.invoke(state, info)
        }
    }

    fun addListener(listener: SocketStateListener): SocketStateListener {
        listeners.add(listener)
        return listener
    }

    fun removeListener(listener: SocketStateListener?) {
       listeners.remove(listener)
    }

    fun isGroupOwner()=null!=server

    companion object {
        const val TAG = "SocketService"
    }
}