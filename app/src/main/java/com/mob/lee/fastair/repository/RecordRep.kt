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
import com.mob.lee.fastair.model.STATE_CHECK
import com.mob.lee.fastair.model.STATE_ORIGIN
import com.mob.lee.fastair.model.TextCategory
import com.mob.lee.fastair.model.VideoCategory
import com.mob.lee.fastair.model.WordCategory
import com.mob.lee.fastair.model.ZipCategory
import com.mob.lee.fastair.utils.updateStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.File

object RecordRep {
    val records = SparseArray<List<Record>>()
    val states = SparseArray<Boolean>()
    var total = 0

    fun load(context : Context?, position : Int) : Channel<Record> {
        val list = records.get(position)

        //有缓存，不查库
        if (list?.isNotEmpty() ?: false) {
            return send(list)
        }
        val channel = Channel<Record>()
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
            channel.close()
        }
        return channel
    }

    fun sortBy(position : Int, selector : (Record) -> Comparable<*>) : Channel<Record>? {
        return operator(position, {
            it.sortedWith(compareByDescending(selector))
        })
    }

    fun reverse(position : Int) : Channel<Record>? {
        return operator(position, { it.reversed() })
    }

    fun states(position : Int, state : Int, start : Int, count : Int) : Channel<Record>? {
        return operator(position, {
            var temp = 0
            it.forEachIndexed { index, record ->
                if (0 == index - start - temp && (temp < count || - 1 == count)) {
                    record.state = state
                    temp += 1
                }
            }
            if (temp == it.size && state == STATE_CHECK) {
                states.put(position, true)
            }
            it
        })
    }

    fun toggleState(position : Int) : Channel<Record>? {
        val isChecked = states.get(position, false)
        val state = if (isChecked) {
            states.remove(position)
            STATE_ORIGIN
        } else {
            STATE_CHECK
        }
        return states(position, state, 0, - 1)
    }

    fun delete(context : Context, position : Int) :Channel<Record>?{
        return operator(position, {
            val datas = it.toMutableList()
            val iter = datas.iterator()
            while (iter.hasNext()) {
                val data = iter.next()
                if (STATE_CHECK==data.state) {
                    val file = File(data.path)
                    if (file.delete()) {
                        iter.remove()
                        updateStorage(context, data)
                    }
                }
            }
            datas
        })
    }

    fun operator(position : Int, op : (List<Record>) -> List<Record>) : Channel<Record>? {
        val datas = records.get(position)
        datas?.let {
            val target = op(it)
            records.put(position, target)
            return send(target)
        }
        return null
    }


    fun send(datas : List<Record?>?) : Channel<Record> {
        val channel = Channel<Record>()
        GlobalScope.launch(Dispatchers.IO) {
            datas?.let {
                for (d in it) {
                    d?.let {
                        channel.send(it)
                    }
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