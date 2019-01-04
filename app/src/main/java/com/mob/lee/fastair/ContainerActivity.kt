package com.mob.lee.fastair

import android.content.ClipData
import android.content.ContentUris
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.mob.lee.fastair.base.AppActivity
import com.mob.lee.fastair.fragment.DiscoverFragment
import com.mob.lee.fastair.fragment.HomeFragment
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.model.STATE_WAIT
import com.mob.lee.fastair.p2p.P2PManager
import com.mob.lee.fastair.utils.database
import java.io.File

/**
 * Created by Andy on 2017/6/2.
 */
class ContainerActivity : AppActivity() {

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        val data = supportParentActivityIntent?.clipData
        if (null != data) {
            parseClipData(data)
        }

        P2PManager.enable.observe({ lifecycle }) {
            if (it) {
                return@observe
            }
            AlertDialog.Builder(this)
                    .setTitle(R.string.title_error)
                    .setMessage(R.string.disconverStateTips)
                    .setPositiveButton(R.string.goTurnOn) { dialog, which ->
                        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                        startActivity(intent)
                    }
                    .setNegativeButton(R.string.exit) { dialog, which ->
                        supportFinishAfterTransition()
                    }
                    .show()
        }

        P2PManager.connected.observe({ lifecycle }) {
            when (it) {
                false ->{
                    AlertDialog.Builder(this)
                            .setTitle(R.string.title_error)
                            .setMessage(R.string.msg_disconnect_or_exit)
                            .setPositiveButton(R.string.reconnect) { dialog, which ->
                                P2PManager.connected.value=null
                            }
                            .setNegativeButton(R.string.exit) { dialog, which ->
                                P2PManager.unregister(this)
                                supportFinishAfterTransition()
                            }
                            .show()
                }

                true -> fragment(HomeFragment::class)

                else ->fragment(DiscoverFragment::class, addToIt = false)

            }
        }
    }

    fun parseClipData(clipData : ClipData) {
        val records = ArrayList<Record>()
        val itemCount = clipData.itemCount
        for (i in 0 until itemCount) {
            val item = clipData.getItemAt(i)
            val uri = item.uri
            if (null != uri && "file".equals(uri.scheme)) {
                val file = File(uri.path)
                val record = Record(
                        ContentUris.parseId(uri),
                        file.length(),
                        file.lastModified(),
                        file.absolutePath,
                        STATE_WAIT)
                records.add(record)
            }
        }
        database(mScope) { dao ->
            dao.insert(records)
        }
    }
}