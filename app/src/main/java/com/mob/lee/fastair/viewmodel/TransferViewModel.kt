package com.mob.lee.fastair.viewmodel

import android.content.Context
import com.mob.lee.fastair.adapter.History
import com.mob.lee.fastair.io.state.FailedState
import com.mob.lee.fastair.io.state.RecordState
import com.mob.lee.fastair.io.state.State
import com.mob.lee.fastair.io.state.SuccessState
import com.mob.lee.fastair.model.DataWrap
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.repository.DataBaseDataSource
import com.mob.lee.fastair.repository.StorageDataSource

class TransferViewModel : AppViewModel() {
    val dataSource by lazy {
        DataBaseDataSource()
    }
    val storageDataSource by lazy {
        StorageDataSource()
    }

    fun histories(context: Context?) = asyncWithWrap {
        dataSource.recordDao(context) {
            DataWrap.success(records().map {
                History(
                    it, when (it.state) {
                        Record.STATE_SUCCESS -> SuccessState(it)
                        else -> FailedState(it)
                    }
                )
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

    fun parseState(state: State): Record? {
        if (state !is RecordState) {
            return null
        }
        return if (state.record.valid()) state.record else null
    }

    companion object{
        @JvmStatic val TAG="TransferViewModel"
    }
}