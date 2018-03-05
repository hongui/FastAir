package com.mob.lee.fastair.service

import android.app.IntentService
import android.app.Notification
import android.content.Intent
import android.os.*
import android.os.Message
import android.util.Log
import com.mob.lee.fastair.R
import com.mob.lee.fastair.io.*
import com.mob.lee.fastair.model.*
import com.mob.lee.fastair.utils.createFile
import com.mob.lee.fastair.utils.database
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.runBlocking
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream
import java.util.*

/**
 * Created by Andy on 2017/12/28.
 */
class FileService:IntentService("FastAir"){

    val MSG_START=1
    val MSG_PROGRESS=2
    val MSG_COMPLITE=3

    var mRecord:Record?=null
    var socket:SocketService?=null
    var mHandler:Handler?=null
    var mFileChangeListener:FileChangeListener?=null

    override fun onCreate() {
        super.onCreate()
        mHandler= Handler({
            if(null==it.obj){
                return@Handler true
            }
            val record=it.obj as Record
            when(it.what){
                MSG_START->{
                    mFileChangeListener?.onStart(record)
                    notification(0, record.name)
                }

                MSG_PROGRESS->{
                    mFileChangeListener?.onProgress(record,it.arg1)
                    notification(it.arg1,record.name)
                }

                MSG_COMPLITE->{
                    mFileChangeListener?.onComplete(record,it.arg1)
                    notification(100,record.name)
                }
            }
            true
        })
    }
    override fun onBind(intent: Intent?): IBinder{
        return BinderImpl(this)
    }

    override fun onHandleIntent(intent: Intent?)= runBlocking {
        if(null==intent&&null!=socket){
            return@runBlocking
        }
        val host=intent?.getStringExtra(ADDRESS)
        val isHost=intent?.getBooleanExtra(IS_HOST,false)?:false
        socket = SocketService()
        socket?.open(PORT_FILE,if(isHost) null else host)
        val records = database().recordDao().waitRecords()
        for (record in records){
            socket?.write(FileSender(record.path))
        }
        var outPut:OutputStream?=null
        var size=-1L
        var temp=0L
        var path=""
        socket?.read{
            if('F'.toByte() ==it.type){
                size=-1
                path=""
                outPut?.close()
                if(null!=mRecord){
                    complete(0)
                }
            }else if('L'.toByte()==it.type){
                size=it.getLong()
                temp=0L
            }else if ('S'.toByte() == it.type) {
                path=it.getString()
                val file=createFile(path)
                outPut=file.outputStream()
                start(file)
            }else{
                outPut?.write(it.bytes())
                temp+=it.bytes().size
                progress(((temp*1.0/size)*100).toInt())
            }
        }
    }


    fun notification(progress:Int,title:String){
        val builder = if(26<=Build.VERSION.SDK_INT){
            Notification.Builder(this,"FastAir")
        }else{
            Notification.Builder(this)
        }
        builder.setContentTitle(title)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setProgress(100,progress,true)
        startForeground(9727,builder.build())
    }

    fun start(file:File){
        mRecord = Record(file.lastModified(), file.length(), file.lastModified(), file.path)
        val msg=Message.obtain(mHandler,MSG_START)
        msg.obj=mRecord
        mHandler?.sendMessage(msg)
    }

    fun progress(progress: Int){
        val msg=Message.obtain(mHandler,MSG_PROGRESS)
        msg.obj=mRecord
        msg.arg1=progress
        mHandler?.sendMessage(msg)
    }

    fun complete(state: Int) {
        val msg=Message.obtain(mHandler,MSG_COMPLITE)
        msg.obj=mRecord
        msg.arg1=state
        mHandler?.sendMessage(msg)
        mRecord?.let {
            it.state= STATE_SUCCESS
            database().recordDao().insert(it)
        }
    }

    inner class FileSender(val path: String) : SendTask {

        override fun exe(): ReceiveChannel<ProtocolBytes> {
            return produce {
                val file = File(path)
                val bytes="F".toByteArray()
                send(ProtocolBytes.wrap(bytes,'F'.toByte()))
                send(ProtocolBytes.string(path.substringAfterLast(File.separator)))
                send(ProtocolBytes.long(file.length()))
                start(file)
                val arr = ByteArray(8192)
                val fis = FileInputStream(file)
                var tempSize=0
                while (true) {
                    val size = fis.read(arr)
                    if (size <= 0) {
                        complete(0)
                        break
                    } else {
                        send(ProtocolBytes.bytes(Arrays.copyOfRange(arr, 0, size)))
                        tempSize+=size
                        progress((tempSize*1.0/file.length()*100).toInt())
                    }
                }
                fis.close()
            }
        }
    }
}