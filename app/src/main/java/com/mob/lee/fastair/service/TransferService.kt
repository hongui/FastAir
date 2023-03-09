package com.mob.lee.fastair.service

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppService
import com.mob.lee.fastair.io.ProtocolByte
import com.mob.lee.fastair.io.SocketService
import com.mob.lee.fastair.io.Writer
import com.mob.lee.fastair.service.Notification.Companion.channel
import com.mob.lee.fastair.service.Notification.Companion.foreground
import com.mob.lee.fastair.viewmodel.DeviceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.channels.SocketChannel

abstract class TransferService : AppService(), SocketService.SocketStateListener {
    abstract var port: Int
    private var mSocket: SocketService? = null

    override fun onBind(intent: Intent?): IBinder {
        doAnotherWork(intent)
        return BinderImpl(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Start Transfer service intent = $intent")
        mSocket?.let { doAnotherWork(intent) } ?: let {
            val (host, isHost) = DeviceViewModel.unBundle(intent?.extras)
            Log.i(TAG, "Start connect with host = $host, isHost = $isHost")
            mSocket = SocketService(true).apply {
                addListener(this@TransferService)
                launch(Dispatchers.IO) {
                    open(this, port, if (isHost) null else host)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Transfer service has destroyed.")
        mSocket?.removeListener(this)
        mSocket?.close()
    }
    fun write(writer: Writer) {
        launch(Dispatchers.IO) {
            mSocket?.write(writer)
        }
    }

    open suspend fun onNewTask(intent: Intent?) {}

    private fun doAnotherWork(intent: Intent?){
        if (null != mSocket && true == mSocket?.isConnected) {
            Log.i(TAG, "Start another work.")
            launch(Dispatchers.IO) { onNewTask(intent) }
        }
    }

    override fun onConnected(channel: SocketChannel,extra:Any?) {
        channel(Notification.FILE_TRANSFER, getString(R.string.transfer_service), NotificationManager.IMPORTANCE_HIGH)
        foreground(Notification.FILE_TRANSFER, Notification.FILE_TRANSFER_CODE) {
            setContentTitle(getString(R.string.transfer_service))
        }
        launch(Dispatchers.IO) {
            onNewTask(extra as Intent?)
        }
    }

    override fun onDisconnected(exception: Exception?) {
        if (Build.VERSION.SDK_INT >= 24) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        mSocket?.removeListener(this)
    }

    override fun onReadStart(channel: SocketChannel) {

    }

    override fun onReadFinished(channel: SocketChannel, data: ProtocolByte) {

    }

    override fun onReadError(channel: SocketChannel, exception: Exception) {
        Log.e(TAG, "Transfer read error = ${exception.message}")
    }

    override fun onWriteStart(channel: SocketChannel, writer: Writer) {

    }

    override fun onWriteFinished(channel: SocketChannel, writer: Writer) {

    }

    override fun onWriteError(channel: SocketChannel, exception: Exception, writer: Writer?) {
        Log.e(TAG, "Transfer write error = ${exception.message}")
    }

    companion object {
        @JvmStatic
        val TAG = "TransferService"
    }
}