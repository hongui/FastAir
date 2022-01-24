package com.mob.lee.fastair.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.mob.lee.fastair.io.http.Http
import com.mob.lee.fastair.localhost.CategoryHandler
import com.mob.lee.fastair.localhost.HomeHandler
import com.mob.lee.fastair.localhost.ImageHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.net.InetSocketAddress
import kotlin.coroutines.CoroutineContext

class HostService() : Service(), CoroutineScope {
    override val coroutineContext: CoroutineContext= SupervisorJob() + Dispatchers.Main.immediate;
    var mHost: Http? = null

    companion object {
        const val PORT = "port"
        const val DEFAULT_PORT = 9527
        fun start(context: Context?, port: Int = DEFAULT_PORT) {
            val intent = Intent(context, HostService::class.java)
            intent.putExtra(PORT, port)
            context?.startService(intent)
        }

        fun stop(context: Context?) {
            val intent = Intent(context, HostService::class.java)
            context?.stopService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val port = intent?.getIntExtra(PORT, DEFAULT_PORT) ?: DEFAULT_PORT
        if(null==mHost){
            mHost=Http(this)
            mHost!!.startLoop(InetSocketAddress(port))
            mHost!!.run {
                addHandler(HomeHandler(this@HostService))
                addHandler(CategoryHandler(this@HostService))
                addHandler(ImageHandler(this@HostService))
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancel()
    }
}