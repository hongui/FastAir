package com.mob.lee.fastair.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.mob.lee.fastair.io.socket.SocketFactory

class HostService : Service() {
    var mHost: SocketFactory? = null

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

        return super.onStartCommand(intent, flags, startId)
    }
}