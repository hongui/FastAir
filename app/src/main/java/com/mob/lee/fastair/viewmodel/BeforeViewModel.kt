package com.mob.lee.fastair.viewmodel

import android.content.Context
import com.mob.lee.fastair.model.DataWrap
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.repository.DataBaseDataSource

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/18 15:34
 * @Description:    无
 */
class BeforeViewModel : AppViewModel() {
    val database by lazy {
        DataBaseDataSource()
    }
    val waitRecords = HashSet<Record>()

    fun waitRecords(context: Context?) = asyncWithWrap {
        database.recordDao(context) {
            val records = waitRecords()
            waitRecords.clear()
            waitRecords.addAll(records)
            DataWrap.success(records)
        }
    }

    fun toggle(record: Record?) {
        record?.state = if (Record.STATE_WAIT == record?.state) {
            Record.STATE_ORIGIN
        } else {
            Record.STATE_WAIT
        }
    }

    fun submit(context: Context?) = asyncWithWrap {
        database.recordDao(context) {
            val records = waitRecords.filter { Record.STATE_WAIT != it.state }
            clearRecord(records)
            DataWrap.success(waitRecords.filter { Record.STATE_WAIT == it.state }.size)
        }
    }
}