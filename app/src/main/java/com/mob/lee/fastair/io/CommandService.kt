package com.mob.lee.fastair.io

import android.content.Intent
import com.mob.lee.fastair.base.AppService
import com.mob.lee.fastair.io.state.STATE_CONNECTED
import com.mob.lee.fastair.io.state.SocketStateListener
import com.mob.lee.fastair.viewmodel.DeviceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommandService:AppService() {
    var mSocket: SocketService? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mSocket?:{
            val (host, isHost) = DeviceViewModel.unBundle(intent?.extras)
            mSocket = SocketService(true)
            var listener: SocketStateListener? = null
            listener = mSocket?.addListener { state, _ ->
                if (STATE_CONNECTED == state) {
                    mSocket?.removeListener(listener)
                    launch(Dispatchers.IO) {
                        connected(mSocket!!)
                        onNewTask(intent)
                    }
                }
            }
            launch(Dispatchers.IO) {
                mSocket?.open(this, COMMAND_PORT, if (isHost) null else host)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    companion object{
        @JvmStatic val COMMAND_PORT=9527
    }
}