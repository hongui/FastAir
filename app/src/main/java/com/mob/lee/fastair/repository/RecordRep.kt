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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

object RecordRep {
    val records = SparseArray<List<Record>>()
    /*
    * HashSet在移除的时候会重新计算hash，导致移除失败，所以只能使用ArrayList
    * */
    val selectRecords = ArrayList<Record>()
    val states = SparseArray<Boolean>()
    var total = 0

    const val DELAY = 8L

    fun load(context: Context?, position: Int): Channel<Record> {
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
                    val temp = ArrayList<Record>(cursor.count)
                    while (cursor.moveToNext()) {
                        val record = category.read(cursor)
                        temp.add(record)
                        delay(DELAY)
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

    fun sortBy(position: Int, selector: (Record) -> Comparable<*>): Channel<Record>? {
        return operator(position, {
            it.sortedWith(compareByDescending(selector))
        })
    }

    fun reverse(position: Int): Channel<Record>? {
        return operator(position, { it.reversed() })
    }

    fun states(position: Int, state: Int, start: Int, count: Int): Channel<Pair<Int, Record?>>? {
        var temp = 0
        return update(position, { index, record, iter ->
            if (0 == index - start - temp && (temp < count || -1 == count)) {
                record.state = state
                temp += 1
                true
            } else {
                false
            }
        })
    }

    fun toggleState(position: Int): Channel<Record>? {
        val isChecked = states.get(position, false)
        val state = if (isChecked) {
            states.remove(position)
            STATE_ORIGIN
        } else {
            states.put(position, true)
            STATE_CHECK
        }
        return operator(position, {
            if (state == STATE_CHECK) {
                selectRecords.addAll(it)
            } else {
                selectRecords.clear()
            }
            it.forEach {
                it.state = state
            }
            it
        })
    }

    fun delete(context: Context, position: Int): Channel<Pair<Int, Record?>>? {
        return update(position, { index, record, iter ->
            if (STATE_CHECK == record.state) {
                val file = File(record.path)
                if (file.delete()) {
                    iter.remove()
                    updateStorage(context, record)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        })
    }

    fun operator(position: Int, op: (List<Record>) -> List<Record>): Channel<Record>? {
        val datas = records.get(position)
        datas?.let {
            val target = op(it)
            records.put(position, target)
            return send(target)
        }
        return null
    }

    fun update(position: Int, action: (index: Int, data: Record, MutableIterator<Record>) -> Boolean): Channel<Pair<Int, Record?>>? {
        val datas = records.get(position)
        datas?.let {
            if (it.isEmpty()) {
                return null
            }
            val channel = Channel<Pair<Int, Record?>>()
            GlobalScope.launch(Dispatchers.IO) {
                val iter = (it as MutableList).iterator()
                var index = 0
                var size = it.size
                var newIndex = 0
                while (iter.hasNext()) {
                    val record = iter.next()
                    val handled = action(index, record, iter)
                    if (handled) {
                        //如果操作更新了记录，则对应需要更新
                        val result = if (size == it.size) {
                            if (record.state == STATE_CHECK) {
                                if (!selectRecords.contains(record)) {
                                    selectRecords.add(record)
                                }
                            } else {
                                selectRecords.remove(record)
                            }
                            record
                        } else {
                            selectRecords.remove(record)
                            null
                        }
                        channel.send(index - newIndex to result)
                        newIndex += size - it.size
                        size = it.size
                        delay(DELAY)
                    }
                    index += 1
                }
                channel.close()
            }
            return channel
        }
        return null
    }

    fun send(datas: List<Record?>?): Channel<Record> {
        val channel = Channel<Record>()
        GlobalScope.launch(Dispatchers.IO) {
            datas?.let {
                for (d in it) {
                    d?.let {
                        channel.send(it)
                        delay(DELAY)
                    }
                }
            }
            channel.close()
        }
        return channel
    }

    fun categories(): Array<Category> {
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