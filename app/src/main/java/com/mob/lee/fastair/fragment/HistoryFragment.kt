package com.mob.lee.fastair.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.recyclerview.widget.LinearLayoutManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.Adapter
import com.mob.lee.fastair.adapter.History
import com.mob.lee.fastair.adapter.HistoryAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.io.state.StartState
import com.mob.lee.fastair.io.state.SuccessState
import com.mob.lee.fastair.io.state.parseFile
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.service.BinderImpl
import com.mob.lee.fastair.service.FileService
import com.mob.lee.fastair.utils.database
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * Created by Andy on 2017/8/31.
 */
class HistoryFragment : AppFragment() {
    private val TAG = "HistoryFragment"
    private var mConntect : ServiceConnection? = null

    override fun layout() : Int = R.layout.fragment_history

    override fun setting() {
        toolbar(R.string.history)

        fragment_history?.layoutManager = LinearLayoutManager(context)
        fragment_history?.adapter = Adapter(HistoryAdapter())
    }

    override fun onResume() {
        super.onResume()
        if (null != mConntect) {
            return
        }
        if (arguments?.getBoolean("isHistory") ?: false) {
            val adapter = fragment_history.adapter as Adapter
            mScope.launch {
                val channel = Channel<List<Record>?>()
                mParent?.database(mScope, { dao ->
                    val records = dao.completedRecords()
                    channel.send(records)
                })
                val records = channel.receive()
                records?.let {
                    for (i in it) {
                        adapter.change(i to SuccessState())
                    }
                }
            }
        } else {
            val intent = Intent(mParent, FileService::class.java)
            intent.putExtras(arguments)

            mConntect = object : ServiceConnection {
                override fun onServiceDisconnected(name : ComponentName?) {

                }

                override fun onServiceConnected(name : ComponentName?, service : IBinder?) {
                    val binder = service as BinderImpl?
                    val fileService = binder?.mService as FileService?
                    fileService?.mFileChangeListener = { state ->
                        val adapter = fragment_history.adapter as Adapter
                        val holder = adapter.get<HistoryAdapter>(0)
                        val index = when (state) {

                            is StartState -> - 1

                            else -> (holder?.datas?.size ?: 1) - 1
                        }
                        parseFile(state)?.let { record ->
                            adapter.change(History(record, state), index)
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
        try {
            if (null != mConntect) {
                mParent?.unbindService(mConntect)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
}