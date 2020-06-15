package com.mob.lee.fastair.repository

import android.content.Context
import androidx.room.Room
import com.mob.lee.fastair.db.AppDatabase
import com.mob.lee.fastair.db.RecordDao
import com.mob.lee.fastair.model.DataWrap

class DataBaseDataSource : DataSource {
    private val database = "fastair"

    suspend fun <D> recordDao(context: Context?, action: suspend RecordDao.() -> DataWrap<D>): DataWrap<D> {
        context ?: return DataWrap(DataWrap.ERROR, msg = "context is null")
        var db: AppDatabase? = null
        return try {
            db = Room.databaseBuilder(context, AppDatabase::class.java, database).build()
            val dao = db.recordDao()
            action(dao)
        } catch (e: Exception) {
            DataWrap(DataWrap.ERROR, msg = e.message)
        } finally {
            db?.close()
        }
    }
}