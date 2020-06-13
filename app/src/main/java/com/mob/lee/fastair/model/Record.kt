package com.mob.lee.fastair.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File

/**
 * Created by Andy on 2017/6/2.
 */
@Entity
data class Record(@PrimaryKey var id: Long, @ColumnInfo var size: Long, @ColumnInfo var date: Long, @ColumnInfo var path: String, @ColumnInfo var state: Int = 0, @ColumnInfo var duration: Long = 0) {

    val name: String
        get() {
            return path.substringAfterLast(File.separator)
        }


    companion object{
        /*文件状态*/
        /*原始状态，即本机文件*/
        const val STATE_ORIGIN=0

        /*选中状态*/
        const val STATE_CHECK=1

        /*准备传输状态*/
        const val STATE_WAIT=2

        /*传输中*/
        const val STATE_TRANSPORT=3

        /*传输成功状态*/
        const val STATE_SUCCESS=4

        /*传输失败状态*/
        const val STATE_FAILED=5
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Record

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return path
    }


}