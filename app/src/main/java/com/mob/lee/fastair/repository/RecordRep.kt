package com.mob.lee.fastair.repository

import android.content.Context
import android.provider.MediaStore
import android.util.SparseArray
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.Comparator

object RecordRep {
    val records = SparseArray<List<Record>>()
    var total = 0

    fun load(context : Context?, position : Int) : Channel<Record?> {
        val list = records.get(position)

        //有缓存，不查库
        if (list?.isNotEmpty() ?: false) {
            return send(list)
        }
        val channel = Channel<Record?>()
        val category = categories()[position]

        //暂时使用全局的Job，不然数据可能会不全
        GlobalScope.launch(Dispatchers.IO) {
            val contentResolver = context?.contentResolver
            val cursor = contentResolver?.query(
                    category.uri(),
                    category.columns(),
                    category.select(),
                    category.value(),
                    MediaStore.MediaColumns.DATE_MODIFIED + " DESC")
            cursor?.let {
                val count = it.count
                //应该可以存得下吧o(*￣▽￣*)ブ
                while (total + count > 50_000) {
                    val key = records.indexOfKey(0)
                    val temp = records.get(key)
                    total -= temp?.size ?: 0
                    records.remove(key)
                }
                it.use {
                    val temp = ArrayList<Record>()
                    while (cursor.moveToNext()) {
                        val record = category.read(cursor)
                        temp.add(record)
                        channel.send(record)
                    }
                    records.put(position, temp)
                    total += temp.size
                }
            }
            channel.send(null)
            channel.close()
        }
        return channel
    }

    fun sortBy(position : Int, selector : (Record) -> Comparable<*>, isAes : Boolean) : Channel<Record?>? {
        return operator(position, {
            val operator = if (isAes) {
                compareBy(selector)
            } else {
                compareByDescending(selector)
            }
            it.sortedWith(operator)
        })
    }

    fun reverse(position : Int) : Channel<Record?>? {
        return operator(position, { it.reversed() })
    }

    fun operator(position : Int, op : (List<Record>) -> List<Record>) : Channel<Record?>? {
        val datas = records.get(position)
        datas?.let {
            val target = op(it)
            records.put(position, target)
            return send(target)
        }
        return null
    }

    fun send(datas : List<Record?>?) : Channel<Record?> {
        val channel = Channel<Record?>()
        GlobalScope.launch(Dispatchers.IO) {
            datas?.let {
                for (d in it) {
                    channel.send(d)
                }
            } ?: let {
                channel.send(null)
            }
            channel.send(null)
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