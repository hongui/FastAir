package com.mob.lee.fastair.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.mob.lee.fastair.ContainerActivity
import com.mob.lee.fastair.R
import com.mob.lee.fastair.io.FileReader
import com.mob.lee.fastair.io.FileWriter
import com.mob.lee.fastair.io.ProcessListener
import com.mob.lee.fastair.io.SocketService
import com.mob.lee.fastair.io.state.*
import com.mob.lee.fastair.model.DataWrap
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.repository.DataBaseDataSource
import com.mob.lee.fastair.utils.updateStorage
import kotlinx.coroutines.async
import java.io.File

/**
 * Created by Andy on 2017/12/28.
 */
class FileService : TransferService() {

    var lastId=0L
    var oldState = 0F
    var mFileChangeListener: ProcessListener? = null
    override var port: Int? = 9527

    val channelId = "fileservice"
    val channelName by lazy {
        getString(R.string.file_transfer)
    }
    val database by lazy {
        DataBaseDataSource()
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        notification(0, getString(R.string.trans_waiting))
    }

    override fun onReceiveMessage(message: State) {
        Log.d(TAG, "Receive file ${message}")
        message.let {
            mFileChangeListener?.invoke(it)
        }
        val file = message.obj as? File
        when (message) {
            is StartState -> {
                notification(0, file?.name ?: "")
            }

            is ProcessState -> {
                notification((message.process / message.total * 100).toInt(), file?.name ?: "")
            }

            is SuccessState -> {
                notification(100, file?.name ?: "")
                async {
                    onNewTask(null)
                }
            }
        }
    }

    override suspend fun onNewTask(intent: Intent?) {
        val records = database.recordDao(this@FileService) {
            DataWrap.success(waitRecords())
        }
        if (records.isSuccess() && true == records.data.isNullOrEmpty()) {
            return
        }
        val target = records.data?.first { lastId!=it.id }
        target?.let { record ->
            Log.e(TAG, "Write File ${record}")
            lastId=record.id
            mSocket?.write(FileWriter(record.path) {
                updateRecord(it, record)
            })
        }

    }

    override suspend fun connected(socket: SocketService) {
        socket.addListener { state, _ ->
            when (state) {
                STATE_DISCONNECTED -> {
                    mSocket?.close()
                    mFileChangeListener?.invoke(FaildState())
                    stopSelf()
                }
            }
        }
        socket.read(FileReader(this@FileService) {
            updateRecord(it)
        })
    }

    fun updateRecord(state: State?, record: Record? = null) {
        when (state) {
            is ProcessState -> {
                if (Math.abs(oldState - state.percentage()) > 8) {
                    oldState = state.percentage()
                    state.sendMessage(mHandler)
                }
            }
            else -> state?.sendMessage(mHandler)
        }

        if (state !is SuccessState) {
            return
        }
        val file = state.obj as? File
        file ?: return
        val target = if (null == record) {
            updateStorage(file.absolutePath)
            Record(
                file.lastModified(),
                file.length(),
                file.lastModified(),
                file.path,
                Record.STATE_SUCCESS,
                state.duration
            )
        } else {
            record.state = Record.STATE_SUCCESS
            record
        }

        async {
            database.recordDao(this@FileService) {
                if (null == record) {
                    insert(target)
                } else {
                    update(target)
                }
                DataWrap.success(null)
            }
        }
    }

    fun notification(progress: Int, title: String,target:Int?=0) {
        val intent=PendingIntent.getActivity(this,9527,
            Intent(this,ContainerActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
        } else {
            Notification.Builder(this)
        }
        builder.setContentTitle(title)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setProgress(100, progress, false)
        builder.setAutoCancel(true)
        builder.setContentIntent(intent)
        val notification = builder.build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(9727, notification)
        startForeground(9727, notification)
    }

    companion object {
        const val TAG = "FileTransfer"
    }
}