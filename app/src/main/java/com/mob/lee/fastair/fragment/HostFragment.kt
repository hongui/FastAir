package com.mob.lee.fastair.fragment

import android.app.Service
import android.content.*
import android.os.IBinder
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.io.getNetIP
import com.mob.lee.fastair.service.BinderImpl
import com.mob.lee.fastair.service.HostService

class HostFragment() : AppFragment(), ServiceConnection {
    val port = 9527
    var mHostService: HostService? = null

    override val layout: Int = R.layout.fragment_host

    override fun setting() {
        title(R.string.local_host)

        val tv_host_ip = view<TextView>(R.id.tv_host_ip)
        tv_host_ip?.text = "${getNetIP(mParent!!)}:${port}"
        HostService.start(requireContext(), port)

        tv_host_ip?.setOnClickListener {
            val manager = ContextCompat.getSystemService(requireContext(), ClipboardManager::class.java)
            manager?.let {
                it.setPrimaryClip(ClipData.newPlainText("ipInfo", tv_host_ip.text.toString()))
            }
        }
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_local_host, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return if (R.id.menu_restart_server == menuItem.itemId) {
                    HostService.start(requireContext(), port, true)
                    true
                } else {
                    false
                }
            }
        }, this)
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(context, HostService::class.java)
        mParent?.bindService(intent, this, Service.BIND_AUTO_CREATE)
    }

    override fun onPause() {
        super.onPause()
        mParent?.unbindService(this)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        (service as BinderImpl?)?.let { binder ->
            mHostService = binder.mService as HostService
            mHostService?.mStatus?.observe(this@HostFragment) {
                switchStatus(it == true)
            }
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        mHostService?.mStatus?.removeObservers(this)
        mHostService = null
    }

    fun switchStatus(isRunning: Boolean) {
        val text = if (isRunning) {
            R.string.server_runing to R.string.stop_server
        } else {
            R.string.server_stoped to R.string.start_server
        }
        val tv_host_status = view<TextView>(R.id.tv_host_status)
        val btn_host_action = view<Button>(R.id.btn_host_action)
        tv_host_status?.setText(text.first)

        btn_host_action?.setOnClickListener {
            if (text.second == R.string.stop_server) {
                mHostService?.stop()
            } else {
                HostService.start(requireContext(), port)
            }
        }
        btn_host_action?.setText(text.second)
    }
}