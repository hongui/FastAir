package com.mob.lee.fastair.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mob.lee.fastair.R
import com.mob.lee.fastair.io.http.Http
import com.mob.lee.fastair.io.socket.ConnectListener
import com.mob.lee.fastair.localhost.*
import com.mob.lee.fastair.service.Notification.Companion.LOCAL_HOST
import com.mob.lee.fastair.service.Notification.Companion.LOCAL_HOST_CODE
import com.mob.lee.fastair.service.Notification.Companion.LOCAL_HOST_NAME
import com.mob.lee.fastair.service.Notification.Companion.cancelNotify
import com.mob.lee.fastair.service.Notification.Companion.channel
import com.mob.lee.fastair.service.Notification.Companion.foreground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.net.InetSocketAddress
import kotlin.coroutines.CoroutineContext

class HostService() : Service(), CoroutineScope {
    override val coroutineContext: CoroutineContext= SupervisorJob() + Dispatchers.Main.immediate;
    var mHost: Http? = null
    val mStatus=MutableLiveData<Boolean>()

    companion object {
        const val PORT = "port"
        const val RESTART = "restart"
        const val DEFAULT_PORT = 9527
        const val TAG = "HostService"
        fun start(context: Context?, port: Int = DEFAULT_PORT,restart:Boolean=false) {
            val intent = Intent(context, HostService::class.java)
            intent.putExtra(PORT, port)
            intent.putExtra(RESTART, restart)
            context?.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        channel(LOCAL_HOST, LOCAL_HOST_NAME)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return BinderImpl(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val port = intent?.getIntExtra(PORT, DEFAULT_PORT) ?: DEFAULT_PORT
        val restart = intent?.getBooleanExtra(RESTART, false) ?: false
        Log.d(TAG,"-------Start Host with port = $port,restart = $restart")
        if(null==mHost){
            start(port)
        }else if(null!=mHost&&restart){
            stop()
            start(port)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    fun start(port:Int){
        mHost=Http(this).apply {
            mListener=object:ConnectListener(){
                override fun onStart() {
                    mStatus.value=true
                    foreground(LOCAL_HOST, LOCAL_HOST_CODE){
                        setContentTitle(getString(R.string.server_runing))
                    }
                }

                override fun onStop() {
                    mStatus.value=false
                }
            }
        }
        mHost!!.startLoop(InetSocketAddress(port))
        mHost!!.run {
            addHandler(HomeHandler(this@HostService))
            addHandler(CategoryHandler(mScope,this@HostService))
            addHandler(ImageHandler(this@HostService))
            addHandler(DownloadHandler())
            addHandler(ChatHandler(this@HostService))
            addHandler(UploadHandler(this@HostService))
        }
    }

    fun stop(){
        mHost?.run {
            stop()
            mStatus.value=false
        }
        mHost=null
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
        coroutineContext.cancel()
    }
}