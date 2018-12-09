package com.mob.lee.fastair.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.HistoryAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.io.state.StartState
import com.mob.lee.fastair.io.state.parseFile
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.service.BinderImpl
import com.mob.lee.fastair.service.FileService
import com.mob.lee.fastair.utils.database
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * Created by Andy on 2017/8/31.
 */
class HistoryFragment : AppFragment() {
    private val TAG = "HistoryFragment"
    private var mConntect : ServiceConnection? = null
    private lateinit var mAdapter : HistoryAdapter

    override fun layout() : Int = R.layout.fragment_history

    override fun setting() {
        toolbar(R.string.history)

        val recyclerView = view?.findViewById<RecyclerView>(R.id.fragment_history)
        mAdapter = HistoryAdapter()
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = mAdapter
    }

    override fun onResume() {
        super.onResume()
        if (arguments?.getBoolean("isHistory") ?: false) {
            /*val channel = Channel<List<Record>?>()
            mParent?.database(mScope, { dao ->
                val records = dao.completedRecords()
                channel.send(records)
            })
            val records = channel.receive()
            records?.let {
                mAdapter.addAll(it)
            }*/

        } else {
            val intent = Intent(mParent, FileService::class.java)
            intent.putExtras(arguments)

            mConntect = object : ServiceConnection {
                override fun onServiceDisconnected(name : ComponentName?) {

                }

                override fun onServiceConnected(name : ComponentName?, service : IBinder?) {
                    val binder = service as BinderImpl?
                    val fileService = binder?.mService as FileService?
                    fileService?.mFileChangeListener = {
                        when (it) {
                            is StartState -> {
                                val record = parseFile(it)
                                record?.let {
                                    mAdapter.setCurrent(it)
                                }
                            }

                            else -> {
                                mAdapter.updateState(it)
                            }
                        }
                    }
                }
            }
            mParent?.bindService(intent, mConntect, Context.BIND_AUTO_CREATE)
            mParent?.startService(intent)
        }
    }

    override fun onStop() {
        super.onStop()
        if (null != mConntect) {
            mParent?.unbindService(mConntect)
        }
    }
}