package com.mob.lee.fastair.fragment

import androidx.lifecycle.coroutineScope
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.io.getNetIP
import com.mob.lee.fastair.io.socket.SocketEventObserver
import com.mob.lee.fastair.io.socket.SocketFactory
import kotlinx.android.synthetic.main.fragment_host.*
import java.net.InetSocketAddress

class HostFragment() : AppFragment() {
    override val layout: Int = R.layout.fragment_host
    lateinit var mSocket: SocketFactory

    override fun setting() {
        mSocket = SocketFactory.open(lifecycle.coroutineScope, 9527)
        mSocket.mDispatcher.add(object : SocketEventObserver {
            override fun onReady(inetSocketAddress: InetSocketAddress) {
                tv_host.text = getNetIP(mParent!!)+inetSocketAddress.toString()
            }
        })
    }

    override fun onStop() {
        super.onStop()
        mSocket.stop()
    }
}