package com.mob.lee.fastair.service

import android.content.Intent
import android.util.Log
import com.mob.lee.fastair.R
import com.mob.lee.fastair.io.FileReader
import com.mob.lee.fastair.io.FileWriter
import com.mob.lee.fastair.io.ProgressListener
import com.mob.lee.fastair.io.ProtocolByte
import com.mob.lee.fastair.io.state.*
import com.mob.lee.fastair.model.DataWrap
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.repository.DataBaseDataSource
import com.mob.lee.fastair.service.Notification.Companion.FILE_TRANSFER
import com.mob.lee.fastair.service.Notification.Companion.FILE_TRANSFER_CODE
import com.mob.lee.fastair.service.Notification.Companion.easyNotify
import com.mob.lee.fastair.utils.updateStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.channels.SocketChannel

/**
 * Created by Andy on 2017/12/28.
 */
class FileService : TransferService(), ProgressListener {
    override var port = 9527

    var mListener: ProgressListener? = null
    private var lastId = 0L
    private val mFileReader by lazy { FileReader(this, this) }

    private val database by lazy {
        DataBaseDataSource()
    }

    override fun onReadFinished(channel: SocketChannel, data: ProtocolByte) {
        mFileReader.invoke(data)
    }

    //Call it when start service or bind service,or finished last transfer
    override suspend fun onNewTask(intent: Intent?) {
        val records = database.recordDao(this@FileService) {
            DataWrap.success(waitRecords())
        }
        if (records.isSuccess() && records.data.isNullOrEmpty()) {
            return
        }
        records.data?.firstOrNull() { lastId != it.id }?.let { record ->
            Log.i(TAG, "Write File ${record}")
            lastId = record.id
            write(FileWriter(record, this))
        }

    }

    fun notification(progress: Int, title: String) {
        easyNotify(FILE_TRANSFER, FILE_TRANSFER_CODE) {
            setContentTitle(getString(R.string.file_transfer))
            setContentText(title)
            setProgress(100, progress, false)
            setAutoCancel(true)
        }
    }

    fun onNewFile(state: StartRecordState) {
        launch(Dispatchers.Main) {
            mListener?.invoke(state)
        }
        Log.i(TAG, "New File start state = ${state.record}")
        //notification(0, state.record.name)
    }

    fun onProgressFile(state: ProcessState) {
        launch(Dispatchers.Main) {
            mListener?.invoke(state)
        }
        //notification((state.percentage() * 100).toInt(), state.record.name)
    }

    fun onFinishedFile(state: SuccessState) {
        //notification(100, state.record.name)
        if (state.fromRemote) {
            Log.i(TAG, "Success receive file state = ${state.record}")
            updateStorage(state.record.path)
        } else {
            Log.i(TAG, "Success send file state = ${state.record}")
        }
        launch(Dispatchers.Main) {
            mListener?.invoke(state)
        }
        onUpdateRecord(state.fromRemote, state.record)
    }

    fun onFailedFile(state: FailedState) {
        Log.e(TAG, "Failed ${if (state.record.valid()) "send" else "receive file state = ${state.record},exception = ${state.exception}"}")
        val record = state.record
        //notification(100, record.name)
        if (0L != record.date && 0L != record.size) {
            onUpdateRecord(false, record)
        }
        launch(Dispatchers.Main) {
            mListener?.invoke(state)
        }
    }

    fun onUpdateRecord(fromRemote: Boolean, record: Record) {
        launch(Dispatchers.IO) {
            database.recordDao(this@FileService) {
                if (fromRemote) {
                    insert(record)
                } else {
                    update(record)
                }
                DataWrap.success(null)
            }
        }
    }

    override fun invoke(state: State) {
        when (state) {
            is StartRecordState -> onNewFile(state)
            is ProcessState -> onProgressFile(state)
            is SuccessState -> onFinishedFile(state)
            else -> onFailedFile(state as FailedState)
        }
    }

    companion object {
        const val TAG = "FileTransfer"
    }
}