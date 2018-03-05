package com.mob.lee.fastair.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.File

/**
 * Created by Andy on 2017/6/2.
 */
@Entity
data class Record(@PrimaryKey var id: Long, @ColumnInfo var size: Long, @ColumnInfo var date: Long, @ColumnInfo var path: String, @ColumnInfo var state: Int = 0, @ColumnInfo var duration: Long = 0) {

    val name: String
        get() {
            val index = path.lastIndexOf(File.separator)
            if (-1 == index) {
                return path
            }
            return path.substring(index+1)
        }
}