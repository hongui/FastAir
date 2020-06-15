package com.mob.lee.fastair.viewmodel

import android.content.Context
import com.mob.lee.fastair.model.DataWrap
import com.mob.lee.fastair.repository.DataBaseDataSource

class BeforeViewModel : AppViewModel() {
    val database by lazy {
        DataBaseDataSource()
    }

    fun files(context: Context?) = asyncWithWrap {
        database.recordDao(context) {
            DataWrap.success(waitRecords())
        }
    }
}