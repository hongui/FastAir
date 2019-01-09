package com.mob.lee.fastair.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.model.STATE_CHECK
import com.mob.lee.fastair.model.STATE_FAILED
import com.mob.lee.fastair.model.STATE_SUCCESS
import com.mob.lee.fastair.model.STATE_WAIT

/**
 * Created by Andy on 2017/12/30.
 */
@Dao
interface RecordDao{

    @Query("SELECT * FROM record")
    fun records():List<Record>

    @Query("SELECT * FROM record WHERE state=${STATE_WAIT}")
    fun waitRecords():List<Record>

    @Query("SELECT * FROM record WHERE state=${STATE_WAIT} limit 1")
    fun waitRecord():Record?

    @Query("SELECT * FROM record WHERE state=${STATE_CHECK}")
    fun checkedRecords():List<Record>

    @Query("SELECT * FROM record WHERE state=${STATE_SUCCESS} OR state=${STATE_FAILED} order by date desc")
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