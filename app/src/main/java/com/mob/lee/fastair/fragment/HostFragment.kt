package com.mob.lee.fastair.fragment

import android.app.Service
import android.content.*
import android.os.IBinder
import android.view.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.io.getNetIP
import com.mob.lee.fastair.service.BinderImpl
import com.mob.lee.fastair.service.HostService
import kotlinx.android.synthetic.main.fragment_host.*

class HostFragment() : AppFragment(), ServiceConnection {
    val port = 9527
    var mHostService: HostService? = null

    override val layout: Int = R.layout.fragment_host

    override fun setting() {
        setHasOptionsMenu(true)
        title(R.string.local_host)

        tv_host_ip.text = "${getNetIP(mParent!!)}:${port}"
        HostService.start(requireContext(), port)

        tv_host_ip.setOnClickListener {
            val manager = ContextCompat.getSystemService(requireContext(), ClipboardManager::class.java)
            manager?.let {
                it.setPrimaryClip(ClipData.newPlainText("ipInfo",tv_host_ip.text.toString()))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_local_host, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_restart_server -> HostService.start(requireContext(), port, true)
        }
        return true
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
        val b = service as BinderImpl?
        b?.run {
            mHostService = mService as HostService
            mHostService?.mStatus?.observe(this@HostFragment,object :Observer<Boolean>{
                override fun onChanged(t: Boolean?) {
                    switchStatus(t==true)
                }
            })
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        mHostService?.mStatus?.removeObservers(this)
        mHostService=null
    }

    fun switchStatus(isRunning:Boolean){
        val text=if(isRunning){
            R.string.server_runing to R.string.stop_server
        }else{
            R.string.server_stoped to R.string.start_server
        }
        tv_host_status.setText(text.first)

        btn_host_action.setOnClickListener {
            if(text.second==R.string.stop_server){
                mHostService?.stop()
            }else{
                HostService.start(requireContext(),port)
            }
        }
        btn_host_action.setText(text.second)
    }
}