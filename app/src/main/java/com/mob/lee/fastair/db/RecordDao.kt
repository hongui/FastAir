package com.mob.lee.fastair.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mob.lee.fastair.model.Record

/**
 * Created by Andy on 2017/12/30.
 */
@Dao
interface RecordDao{

    @Query("SELECT * FROM record")
    suspend fun records():List<Record>

    @Query("SELECT * FROM record WHERE state=${Record.STATE_WAIT}")
    suspend fun waitRecords():List<Record>

    @Query("SELECT * FROM record WHERE state=${Record.STATE_WAIT} limit 1")
    suspend fun waitRecord():Record?

    @Query("SELECT * FROM record WHERE state=${Record.STATE_CHECK}")
    suspend fun checkedRecords():List<Record>

    @Query("SELECT * FROM record WHERE state=${Record.STATE_SUCCESS} OR state=${Record.STATE_FAILED} order by date desc")
    suspend fun completedRecords():List<Record>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updates(records:List<Record>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(record:Record)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record:Record)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(records:Collection<Record>)

    @Delete()
    suspend fun clearRecord(records : List<Record>)
}