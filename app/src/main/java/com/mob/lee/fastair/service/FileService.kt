package com.mob.lee.fastair.service

import android.app.IntentService
import android.app.Notification
import android.content.Intent
import android.os.*
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AndroidScope
import com.mob.lee.fastair.io.*
import com.mob.lee.fastair.io.state.ProcessState
import com.mob.lee.fastair.io.state.StartState
import com.mob.lee.fastair.io.state.State
import com.mob.lee.fastair.io.state.SuccessState
import com.mob.lee.fastair.model.*
import com.mob.lee.fastair.utils.database
import java.io.File

/**
 * Created by Andy on 2017/12/28.
 */
class FileService : IntentService("FastAir") {
    var socket: SocketService? = null
    var mHandler: Handler? = null
    var mFileChangeListener: ProcessListener? = null

    val mScope: AndroidScope by lazy {
        AndroidScope()
    }

    override fun onCreate() {
        super.onCreate()
        mHandler = Handler({
            if (null == it.obj) {
                return@Handler true
            }
            val state = it.obj as? State
            state?.let {
                mFileChangeListener?.invoke(it)
            }
            val file = state?.obj as? File
            when (state) {
                is StartState -> {
                    notification(0, file?.name ?: "")
                }

                is ProcessState -> {
                    notification((state.process / state.total * 100).toInt(), file?.name ?: "")
                }

                is SuccessState -> {
                    notification(100, file?.name ?: "")
                }
            }
            true
        })
        mScope.create()
    }

    override fun onDestroy() {
        super.onDestroy()
        mScope.destory()
    }

    override fun onBind(intent: Intent?): IBinder {
        return BinderImpl(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        if (null == intent && null != socket) {
            return
        }
        val host = intent?.getStringExtra(ADDRESS)
        val isHost = intent?.getBooleanExtra(IS_HOST, false) ?: false
        socket = SocketService(mScope)
        socket?.open(PORT_FILE, if (isHost) null else host)
        val records = database().recordDao().waitRecords()
        for (record in records) {
            socket?.write(FileWriter(record.path))
        }
        socket?.read(FileReader(this) {
            it.sendMessage(mHandler)
            if (it is SuccessState) {
                val file = it.obj as? File
                file ?: return@FileReader
                val record = Record(file.lastModified(), file.length(), file.lastModified(), file.path)
                database().recordDao().insert(record)
            }
        })
    }


    fun notification(progress: Int, title: String) {
        val builder = if (26 <= Build.VERSION.SDK_INT) {
            Notification.Builder(this, "FastAir")
        } else {
            Notification.Builder(this)
        }
        builder.setContentTitle(title)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setProgress(100, progress, true)
        startForeground(9727, builder.build())
    }
}