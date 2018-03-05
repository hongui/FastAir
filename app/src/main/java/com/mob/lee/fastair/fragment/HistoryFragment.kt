package com.mob.lee.fastair.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.HistoryAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.base.OnBackpressEvent
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.service.BinderImpl
import com.mob.lee.fastair.service.FileChangeListener
import com.mob.lee.fastair.service.FileService
import com.mob.lee.fastair.utils.database
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

/**
 * Created by Andy on 2017/8/31.
 */
class HistoryFragment:AppFragment(),FileChangeListener{
    private val TAG="HistoryFragment"
    private var mConntect:ServiceConnection?=null
    private lateinit var mAdapter:HistoryAdapter

    override fun onStart(record: Record) {
        mAdapter.setCurrent(record)
    }

    override fun onProgress(record: Record, progress: Int) {
        mAdapter.setProgress(progress)
    }

    override fun onComplete(record: Record, state: Int) {
        mAdapter.setComplete(state)
    }

    override fun layout(): Int = R.layout.fragment_history

    override fun setting() {
        toolbar(R.string.history)

        val recyclerView=view?.findViewById<RecyclerView>(R.id.fragment_history)
        mAdapter=HistoryAdapter()
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter=mAdapter
    }

    override fun onResume() {
        super.onResume()
        if(arguments?.getBoolean("isHistory")?:false){
            runBlocking {
                val channel=Channel<List<Record>?>()
                async {
                    val records = mParent?.database()?.recordDao()?.completedRecords()
                    channel.send(records)
                }
                val records=channel.receive()
                records?.let {
                    mAdapter.addAll(it)
                }
            }


        }else {
            val intent = Intent(mParent, FileService::class.java)
            intent.putExtras(arguments)

            mConntect = object : ServiceConnection {
                override fun onServiceDisconnected(name: ComponentName?) {

                }

                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    val binder = service as BinderImpl?
                    val fileService = binder?.mService as FileService?
                    fileService?.mFileChangeListener = this@HistoryFragment
                }
            }
            mParent?.bindService(intent, mConntect, Context.BIND_AUTO_CREATE)
            mParent?.startService(intent)
        }
    }
    override fun onStop() {
        super.onStop()
        if(null!=mConntect) {
            mParent?.unbindService(mConntect)
        }
    }
}