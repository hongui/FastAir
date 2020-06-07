package com.mob.lee.fastair.viewmodel

import android.content.Context
import com.mob.lee.fastair.model.DataWrap
import com.mob.lee.fastair.repository.DataBaseDataSource

class HistoryViewModel : AppViewModel() {
    val dataSource by lazy {
        DataBaseDataSource()
    }

    fun history(context: Context?) =
            async{
                dataSource.recordDao(context) {
                    DataWrap(data = completedRecords())
                }
            }

}