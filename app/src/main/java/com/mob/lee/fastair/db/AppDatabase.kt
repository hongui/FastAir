package com.mob.lee.fastair.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mob.lee.fastair.model.Record

/**
 * Created by Andy on 2017/12/30.
 */
@Database(entities = arrayOf(Record::class),version = 1,exportSchema = false)
abstract class AppDatabase: RoomDatabase(){

    abstract fun recordDao():RecordDao
}