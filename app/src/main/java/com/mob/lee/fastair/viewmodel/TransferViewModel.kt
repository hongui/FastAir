package com.mob.lee.fastair.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.History
import com.mob.lee.fastair.model.DataWrap
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.repository.DataBaseDataSource
import com.mob.lee.fastair.repository.StorageDataSource
import com.mob.lee.fastair.utils.database
import com.mob.lee.fastair.utils.updateStorage
import kotlinx.coroutines.GlobalScope
import java.io.File

class TransferViewModel : AppViewModel() {
    val dataSource by lazy {
        DataBaseDataSource()
    }
    val storageDataSource by lazy {
        StorageDataSource()
    }
    fun history(context: Context?) = async<List<History>>{
                /*dataSource.recordDao(context) {
                    completedRecords()
                }*/
            }

    fun update(record: Record?){

    }

    //暂时没必要坐
    fun clear(context: Context?){
        /*val key = context?.getString(R.string.key_default_clear)
        val clear = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, true)
        if (clear) {
            dataSource?.recordDao(context){
                clearRecord()
            }
        }
        DataWrap.success("")*/
    }

    fun rename(context: Context?,record:Record?)=asyncWithWrap{
        storageDataSource.updateStorage(context,record?.path)
        dataSource.recordDao(context){
            update(record)
            DataWrap.success(0)
        }
    }
}