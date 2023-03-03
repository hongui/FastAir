package com.mob.lee.fastair.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.RecordAdapter
import com.mob.lee.fastair.io.ProcessListener
import com.mob.lee.fastair.io.state.State
import com.mob.lee.fastair.io.state.parseFile
import com.mob.lee.fastair.service.BinderImpl
import com.mob.lee.fastair.service.FileService
import com.mob.lee.fastair.utils.errorToast
import com.mob.lee.fastair.viewmodel.DeviceViewModel
import com.mob.lee.fastair.viewmodel.TransferViewModel

class TransferFragment : ConnectFragment(), ProcessListener {
    override val layout: Int = R.layout.fragment_recyclerview
    private var mConntect: ServiceConnection? = null
    private lateinit var mAdapter: RecordAdapter
    val viewModel by lazy {
        viewModel<TransferViewModel>()
    }

    override fun setting() {
        title(R.string.file_transfer)

        mAdapter = RecordAdapter {
            viewModel.rename(requireContext(), it)
        }
        view<RecyclerView>(R.id.rv_recyclerview)?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

        Intent(requireContext(), FileService::class.java).apply {
            val device:DeviceViewModel by requireActivity().viewModels()
            putExtras(device.bundle())
            mConntect = object : ServiceConnection {
                override fun onServiceDisconnected(name: ComponentName?) {

                }

                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    val binder = service as BinderImpl?
                    val fileService = binder?.mService as FileService?
                    fileService?.mFileChangeListener = this@TransferFragment
                }
            }
            mParent?.startService(this)
            requireActivity().bindService(this, mConntect!!, Context.BIND_IMPORTANT)
        }


    }

    override fun onStop() {
        super.onStop()
        try {
            mConntect?.let {
                mParent?.unbindService(it)
            }
            mConntect = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun invoke(state: State) {
        val record = parseFile(state)
        if (null == record) {
            mParent?.errorToast(R.string.disconnected)
            return
        }
        viewModel.transSpeed(state)
        mAdapter.update(state, record)
    }
}