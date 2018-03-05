package com.mob.lee.fastair.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import com.mob.lee.fastair.io.SocketService
import com.mob.lee.fastair.io.StringSender
import com.mob.lee.fastair.model.ADDRESS
import com.mob.lee.fastair.model.IS_HOST
import com.mob.lee.fastair.model.PORT_FILE
import com.mob.lee.fastair.model.PORT_MESSAGE
import kotlinx.coroutines.experimental.runBlocking
import kotlin.concurrent.thread

/**
 * Created by Andy on 2017/8/29.
 */
class MessageService : Service() {
    val ON_READ=1

    var socket:SocketService?=null
    var mHandler: Handler?=null
    var mMessageListener:MessageChangeListener?=null

    override fun onCreate() {
        super.onCreate()
        mHandler=Handler{
            if(ON_READ==it.what&&null!=it.obj){
                mMessageListener?.onReaded(it.obj as String)
            }
            true
        }
    }
    override fun onBind(intent: Intent?): IBinder {
        return BinderImpl(this)
    }

    fun write(content:String){
        socket?.write(StringSender(content))
    }

    fun close(){
        socket?.close()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int{
        if(null==intent&&null!=socket){
            return super.onStartCommand(intent, flags, startId)
        }
        val host=intent?.getStringExtra(ADDRESS)
        val isHost=intent?.getBooleanExtra(IS_HOST,false)?:false
        socket = SocketService(true)
        socket?.open(PORT_MESSAGE,if(isHost) null else host)
        socket?.read {
            val content=it.getString()
            val msg=Message.obtain(mHandler,ON_READ)
            msg.obj=content
            mHandler?.sendMessage(msg)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        socket?.close()
    }
}