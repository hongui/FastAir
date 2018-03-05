package com.mob.lee.fastair

import android.arch.persistence.room.Room
import android.content.ClipData
import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import com.mob.lee.fastair.base.AppActivity
import com.mob.lee.fastair.db.AppDatabase
import com.mob.lee.fastair.fragment.DiscoverFragment
import com.mob.lee.fastair.fragment.HomeFragment
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.model.Message
import com.mob.lee.fastair.model.STATE_ORIGIN
import com.mob.lee.fastair.p2p.P2PManager
import com.mob.lee.fastair.utils.database
import java.io.File
import kotlin.concurrent.thread

/**
 * Created by Andy on 2017/6/2.
 */
class ContainerActivity : AppActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = supportParentActivityIntent?.clipData
        if (null != data) {
            parseClipData(data)
        } else {
            fragment(HomeFragment::class)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        P2PManager.removeService(this)
    }

    fun parseClipData(clipData: ClipData) {
        val records = ArrayList<Record>()
        val itemCount = clipData.itemCount
        for (i in 0 until itemCount) {
            val item = clipData.getItemAt(i)
            val uri = item.uri
            if (null != uri && "file".equals(uri.scheme)) {
                val file=File(uri.path)
                val record = Record(
                        ContentUris.parseId(uri),
                        file.length(),
                        file.lastModified(),
                        file.absolutePath)
                records.add(record)
            }
        }
        thread {
            database().recordDao().insert(records)
        }
    }
}