package com.mob.lee.fastair.repository

import android.content.Context
import android.provider.MediaStore
import android.util.SparseArray
import com.mob.lee.fastair.base.AndroidScope
import com.mob.lee.fastair.model.ApplicationCategory
import com.mob.lee.fastair.model.Category
import com.mob.lee.fastair.model.ExcelCategory
import com.mob.lee.fastair.model.ImageCategory
import com.mob.lee.fastair.model.MusicCategory
import com.mob.lee.fastair.model.OtherCategory
import com.mob.lee.fastair.model.PDFCategory
import com.mob.lee.fastair.model.PowerPointCategory
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.model.TextCategory
import com.mob.lee.fastair.model.VideoCategory
import com.mob.lee.fastair.model.WordCategory
import com.mob.lee.fastair.model.ZipCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

object RecordRep {
    val records = SparseArray<List<Record>>()
    var total=0

    suspend fun load(scope : AndroidScope, context : Context?, position : Int) : Channel<Record> {
        val channel = Channel<Record>()
        val list = records.get(position)
        if (list?.isNotEmpty()?:false) {
            list.forEach {
                channel.send(it)
            }
            channel.close()
        }
        records.clear()
        val category = categories()[position]


        scope.launch(Dispatchers.IO) {
            val contentResolver = context?.contentResolver
            val cursor = contentResolver?.query(
                    category.uri(),
                    category.columns(),
                    category.select(),
                    category.value(),
                    MediaStore.MediaColumns.DATE_MODIFIED + " DESC")
            cursor?.let {
                val count=it.count
                while (total+count>10_000){
                    val key=records.indexOfKey(0)
                    val temp=records.get(key)
                    total-=temp?.size?:0
                    records.remove(key)
                }
                it.use {
                    val temp=ArrayList<Record>()
                    while (cursor.moveToNext()) {
                        val record = category.read(cursor)
                        temp.add(record)
                        channel.send(record)
                    }
                    records.put(position,temp)
                    total+=temp.size
                }
            }
            channel.close()
        }
        return channel
    }

    fun categories() : Array<Category> {
        return arrayOf(ImageCategory(),
                MusicCategory(),
                VideoCategory(),
                WordCategory(),
                ExcelCategory(),
                PowerPointCategory(),
                TextCategory(),
                PDFCategory(),
                ApplicationCategory(),
                ZipCategory(),
                OtherCategory())
    }
}