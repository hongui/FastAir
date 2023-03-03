package com.mob.lee.fastair.viewmodel

import android.content.Context
import android.util.Log
import com.mob.lee.fastair.adapter.History
import com.mob.lee.fastair.io.state.FaildState
import com.mob.lee.fastair.io.state.ProcessState
import com.mob.lee.fastair.io.state.State
import com.mob.lee.fastair.io.state.SuccessState
import com.mob.lee.fastair.model.DataWrap
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.repository.DataBaseDataSource
import com.mob.lee.fastair.repository.StorageDataSource

class TransferViewModel : AppViewModel() {
    var lastState:State?=null
    var lastDate:Long=0
    val dataSource by lazy {
        DataBaseDataSource()
    }
    val storageDataSource by lazy {
        StorageDataSource()
    }

    fun histories(context: Context?) = asyncWithWrap {
        dataSource.recordDao(context) {
            DataWrap.success(records().map {
                History(it, when (it.state) {
                    Record.STATE_SUCCESS -> SuccessState()
                    else -> FaildState()
                })
            })
        }
    }

    fun update(record: Record?) {

    }

    //暂时没必要坐
    fun clear(context: Context?) {
        /*val key = context?.getString(R.string.key_default_clear)
        val clear = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, true)
        if (clear) {
            dataSource?.recordDao(context){
                clearRecord()
            }
        }
        DataWrap.success("")*/
    }

    fun rename(context: Context?, record: Record?) = asyncWithWrap {
        storageDataSource.updateStorage(context, record?.path)
        dataSource.recordDao(context) {
            update(record)
            DataWrap.success(0)
        }
    }

    fun transSpeed(state:State){
        if(null==lastState){
            lastState=state
            lastDate=System.currentTimeMillis()
        }
        if(lastState is ProcessState && state is ProcessState){
            val data=state.process-(lastState as ProcessState).process
            val date=System.currentTimeMillis()-lastDate
            Log.e("TAG",((data/1024)/(date/1000)).toString())
        }
        lastState=state
        lastDate=System.currentTimeMillis()
    }
}