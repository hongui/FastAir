package com.mob.lee.fastair.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.SparseArray
import com.mob.lee.fastair.model.*
import com.mob.lee.fastair.utils.updateStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.File

class StorageDataSource : DataSource {
    companion object {
        const val DELAY = 8L

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
                    ZipCategory())
        }
    }

    val states = SparseArray<Boolean>()
    var total = 0
    val records = SparseArray<List<Record>>()

    /**
     * HashSet在移除的时候会重新计算hash，导致移除失败，所以只能使用ArrayList
     * */
    val selectRecords = ArrayList<Record>()

    /**
     *  Fetch media info
     *  if there are any cache in records,just return the cache,otherwise launch a task to fetch info with category
     */
    fun fetch(context: Context?,scope:CoroutineScope, position: Int): Channel<Record> {
        val list = records.get(position)

        //return cache
        if (true == list?.isNotEmpty()) {
            return send(list)
        }

        val channel = Channel<Record>()
        val category = categories()[position]

        scope.launch(Dispatchers.IO) {
            val contentResolver = context?.contentResolver
            val cursor = contentResolver?.query(
                    category.uri(),
                    category.columns(),
                    category.select(),
                    category.value(),
                    "${MediaStore.MediaColumns.DATE_MODIFIED} DESC")
            cursor?.use {
                val count = it.count
                //应该可以存得下吧o(*￣▽￣*)ブ
                while (total + count > 50_000) {
                    val key = records.keyAt(0)
                    val temp = records.get(key)
                    total -= temp?.size ?: 0
                    records.remove(key)
                }
                it.use {
                    val temp = ArrayList<Record>(cursor.count)
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


    suspend fun delete(context: Context?, record: Record): Boolean {
        val file = File(record.path)
        return if (file.delete()) {
            context?.updateStorage(record.path)
            true
        } else {
            false
        }
    }

    fun send(datas: List<Record?>?): Channel<Record> {
        val channel = Channel<Record>()
        GlobalScope.launch(Dispatchers.IO) {
            datas?.let {
                for (d in it) {
                    d?.let {
                        channel.send(it)
                    }
                    delay(DELAY)
                }
            }
            channel.close()
        }
        return channel
    }

    fun updateStorage(context: Context?,path:String?){
        path ?: return
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.fromFile(File(path))
        context?.sendBroadcast(intent)
    }
}