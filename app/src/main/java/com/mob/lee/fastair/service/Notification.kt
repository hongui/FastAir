package com.mob.lee.fastair.service

import android.app.*
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.mob.lee.fastair.ContainerActivity
import com.mob.lee.fastair.R

class Notification {
    companion object {
        const val FILE_TRANSFER = "file_transfor"
        const val FILE_TRANSFER_NAME = "file_transfor"
        const val FILE_TRANSFER_CODE = 9527
        const val LOCAL_HOST = "local_host"
        const val LOCAL_HOST_NAME = "local_host"
        const val LOCAL_HOST_CODE = 9528

        fun Context.channel(id: String, name: String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT)
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)
            }
        }

        fun Context.easyNotify(channelId: String, code: Int, builder: Notification.Builder.() -> Unit):Notification {
            val intent = PendingIntent.getActivity(
                this, code,
                Intent(this, ContainerActivity::class.java),
                if(Build.VERSION.SDK_INT>=31) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
            )
            val b = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Builder(this, channelId)
            } else {
                Notification.Builder(this)
            }
            b.setSmallIcon(R.drawable.ic_launcher_foreground)
            b.setColor(ContextCompat.getColor(this,R.color.colorAccent))
            b.setContentIntent(intent)
            b.setAutoCancel(true)
            builder(b)
            val notification = b.build()

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.notify(code, notification)
            return notification
        }

        fun Service.foreground(channelId: String, code: Int, builder: Notification.Builder.() -> Unit){
            easyNotify(channelId, code, builder).let {
                startForeground(code,it)
            }
        }

        fun Context.cancelNotify(id:Int){
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.cancel(id)
        }
    }
}