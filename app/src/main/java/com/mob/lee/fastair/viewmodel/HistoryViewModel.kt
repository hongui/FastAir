package com.mob.lee.fastair.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import com.mob.lee.fastair.model.DataWrap
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.repository.DataBaseDataSource

class HistoryViewModel : AppViewModel() {
    val dataSource by lazy {
        DataBaseDataSource()
    }

    fun history(context: Context?) = async<List<Record>>{
                /*dataSource.recordDao(context) {
                    completedRecords()
                }*/
            }

}