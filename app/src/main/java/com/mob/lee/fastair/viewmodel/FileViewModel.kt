package com.mob.lee.fastair.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mob.lee.fastair.model.APPLICATION
import com.mob.lee.fastair.model.EXCEL
import com.mob.lee.fastair.model.MUSIC
import com.mob.lee.fastair.model.PDF
import com.mob.lee.fastair.model.PICTURE
import com.mob.lee.fastair.model.POWERPOINT
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.model.STATE_CHECK
import com.mob.lee.fastair.model.TXT
import com.mob.lee.fastair.model.VIDEO
import com.mob.lee.fastair.model.WORD
import com.mob.lee.fastair.model.ZIP
import kotlin.concurrent.thread

/**
 * Created by Andy on 2017/11/13.
 */
class FileViewModel : ViewModel() {
    val mRecords = MutableLiveData<List<Record>>()
    var mCategory = 0
    var mCheckAll =false
    get() = mRecords.value?.all { STATE_CHECK==it.state }?:false
    var mIsDes =true

    fun load(context: Context, category: Int) {
        if (null != mRecords.value && mCategory == category) {
            return
        }
        if (null != mRecords.value) {
            mRecords.value = emptyList()
        }
        mCategory = category
        thread {
            val single = MutableLiveData<Record>()
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(
                    uri(category),
                    columns(),
                    selection(category),
                    values(category),
                    MediaStore.MediaColumns.DATE_MODIFIED + " DESC")

            cursor.use {
                val records = ArrayList<Record>()
                while (cursor.moveToNext()) {
                    val record = Record(
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)),
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)),
                            cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)),
                            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)))
                    records.add(record)
                    if (0 == records.size % 10) {
                        mRecords.postValue(records)
                    }
                }
                mRecords.postValue(records)
            }
        }
    }

    fun checkedrecords(): List<Record> {
        return mRecords.value?.filter { STATE_CHECK == it.state } ?: emptyList()
    }

    private fun uri(category: Int): Uri {
        when (category) {
            MUSIC -> return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            VIDEO -> return MediaStore.Video.Media.EXTERNAL_CONTENT_URI

            PICTURE -> return MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            else -> return MediaStore.Files.getContentUri("external")
        }
    }

    private fun columns(): Array<String> {
        return arrayOf(
                /*id*/
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DATE_MODIFIED,
                /*路径*/
                MediaStore.MediaColumns.DATA)
    }

    private fun selection(category: Int): String? {
        when (category) {
            WORD -> return MediaStore.MediaColumns.DATA +
                    " LIKE ? OR " + MediaStore.MediaColumns.DATA +
                    " LIKE ?"

            EXCEL -> return MediaStore.MediaColumns.DATA +
                    " LIKE ? OR " +
                    MediaStore.MediaColumns.DATA +
                    " LIKE ?"

            POWERPOINT -> return MediaStore.MediaColumns.DATA +
                    " LIKE ? OR " +
                    MediaStore.MediaColumns.DATA +
                    " LIKE ?"

            PDF -> return MediaStore.MediaColumns.DATA + " LIKE ?"

            TXT -> return MediaStore.MediaColumns.DATA + " LIKE ?"

            ZIP -> return MediaStore.MediaColumns.DATA + " LIKE ? OR " +
                    MediaStore.MediaColumns.DATA +
                    " LIKE ? OR " +
                    MediaStore.MediaColumns.DATA +
                    " LIKE ?"

            APPLICATION -> return MediaStore.MediaColumns.DATA + " LIKE ?"

            else -> return null
        }
    }

    private fun values(category: Int): Array<String>? {
        when (category) {
            WORD -> return arrayOf("%.doc", "%.docx")

            EXCEL -> return arrayOf("%.xls", "%.xlsx")

            POWERPOINT -> return arrayOf("%.ppt", "%.pptx")

            PDF -> return arrayOf("%.pdf")

            TXT -> return arrayOf("%.txt")

            ZIP -> return arrayOf("%.zip", "%.rar", "%.7z")

            APPLICATION -> return arrayOf("%.apk")

            else -> return null
        }
    }
}