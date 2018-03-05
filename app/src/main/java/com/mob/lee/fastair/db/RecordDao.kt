package com.mob.lee.fastair.db

import android.arch.persistence.room.*
import com.mob.lee.fastair.model.*

/**
 * Created by Andy on 2017/12/30.
 */
@Dao
interface RecordDao{

    @Query("SELECT * FROM Record")
    fun records():List<Record>

    @Query("SELECT * FROM Record WHERE state=${STATE_WAIT}")
    fun waitRecords():List<Record>

    @Query("SELECT * FROM Record WHERE state=${STATE_CHECK}")
    fun checkedRecords():List<Record>

    @Query("SELECT * FROM Record WHERE state=${STATE_SUCCESS} OR state=${STATE_FAILED}")
    fun completedRecords():List<Record>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updates(records:List<Record>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(record:Record)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg records:Record)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(records:List<Record>)
}