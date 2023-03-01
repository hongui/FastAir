package com.mob.lee.fastair.model

import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

abstract class Category {
    abstract val title:Int

    /**
     * 查找的Uri路径，默认为外部存储
     */
    open fun uri() : Uri {
        return MediaStore.Files.getContentUri(if(Build.VERSION.SDK_INT>=29) MediaStore.VOLUME_EXTERNAL else "external")
    }

    /**
     * 查找字段
     */
    open fun columns() : Array<String> {
        return arrayOf(
                /*id*/
                MediaStore.MediaColumns._ID,
                /*大小*/
                MediaStore.MediaColumns.SIZE,
                /*修改日期*/
                MediaStore.MediaColumns.DATE_MODIFIED,
                /*路径*/
                MediaStore.MediaColumns.DATA)
    }

    /**
     * 条件
     */
    open fun select() : String? {
        return null
    }

    /**
     * 条件取值
     */
    open fun value() : Array<String>? {
        return null
    }

    /**
     * 组装
     */
    open fun read(cursor : Cursor) : Record {
        return Record(
                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)),
                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))*1000,
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)))
    }
}