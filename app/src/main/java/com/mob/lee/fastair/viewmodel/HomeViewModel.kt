package com.mob.lee.fastair.viewmodel

import android.Manifest
import android.content.ClipData
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.model.DataWrap
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.repository.DataBaseDataSource
import com.mob.lee.fastair.repository.StorageDataSource
import com.mob.lee.fastair.utils.dialog
import com.mob.lee.fastair.utils.errorToast
import com.mob.lee.fastair.utils.urlToPath
import kotlinx.coroutines.channels.Channel
import java.io.File

class HomeViewModel : AppViewModel() {
    var isDes = true
    val recordLiveData = MutableLiveData<Record>()
    val hasSelectedLiveData = MutableLiveData<Boolean>()
    var position: Int = -1
    val records = ArrayList<Record>()
    val selectedRecords = HashSet<Record>()

    val dataSource by lazy {
        StorageDataSource()
    }
    val database by lazy {
        DataBaseDataSource()
    }
    var currentChannel: Channel<Record>? = null

    fun parseClip() {

    }

    fun updateLocation(fragment: AppFragment, location: Int) {
        position = location
        if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
            fragment.mParent?.dialog {
                setMessage(R.string.no_permission_to_scan)
                    .setPositiveButton(R.string.turn_on) { _, _ ->
                        withStoragePermissionROrHigher(
                            fragment,
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE
                        ) {
                            fragment.requireContext().errorToast(R.string.no_permission_to_scan)
                        }
                    }.setNegativeButton(R.string.exit) { _, _ ->
                        fragment.mParent?.finish()
                    }
            }
        } else if (Build.VERSION.SDK_INT >= 30 && Environment.isExternalStorageManager()) {
            fetch(fragment.context)
        } else {
            withPermission(
                fragment,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                action = { hasPermission ->
                    if (hasPermission) {
                        fetch(fragment.context)
                    } else {
                        fragment.requireContext().errorToast(R.string.no_permission_to_scan)
                        fragment.mParent?.dialog {
                            setMessage(R.string.no_permission_to_scan)
                                .setPositiveButton(R.string.turn_on) { _, _ ->
                                    openSetting(fragment)
                                }.setNegativeButton(R.string.exit) { _, _ ->
                                    fragment.mParent?.finish()
                                }
                        }
                    }
                })
        }
    }

    fun fetch(context: Context?) = async(recordLiveData) {
        currentChannel?.cancel()
        records.clear()

        val channel = dataSource.fetch(context, viewModelScope, position)
        currentChannel = channel
        for (r in channel) {
            next(r)
            records.add(r)
        }
    }


    inline fun <D : Comparable<D>> sortBy(crossinline selector: (Record) -> D) =
        async(recordLiveData) {
            if (isDes) {
                records.sortByDescending(selector)
            } else {
                records.sortBy(selector)
            }
            for (r in records) {
                next(r)
            }
        }


    fun reverse() = async(recordLiveData) {
        isDes = !isDes
        records.reverse()
        for (r in records) {
            next(r)
        }
    }

    fun delete(context: Context?) = async(recordLiveData) {
        val checkedRecords = checkedRecords()
        for (r in checkedRecords) {
            val removed = dataSource.delete(context, r)
            if (removed) {
                records.remove(r)
            }
        }
        update()
    }

    fun update() = async(recordLiveData) {
        records.forEach { next(it) }
    }

    fun selectAll() = async(recordLiveData) {
        val hasChecked = checkedRecords().isNotEmpty()
        hasSelectedLiveData.value = !hasChecked
        records.forEach {
            it.state = if (hasChecked) {
                selectedRecords.remove(it)
                Record.STATE_ORIGIN
            } else {
                selectedRecords.add(it)
                Record.STATE_CHECK
            }
            next(it)
        }
    }

    fun toggleState(record: Record) {
        record.state = if (Record.STATE_CHECK == record.state) {
            selectedRecords.remove(record)
            Record.STATE_ORIGIN
        } else {
            selectedRecords.add(record)
            Record.STATE_CHECK
        }
        hasSelectedLiveData.value = selectedRecords.isNotEmpty()
    }

    fun checkedRecords(): Set<Record> {
        return selectedRecords
    }

    fun sortByName() {
        sortBy { it.name }
    }

    fun sortBySize() {
        sortBy { it.size }
    }

    fun sortByDate() {
        sortBy { it.date }
    }

    fun write(context: Context?) = asyncWithWrap<String> {
        database.recordDao(context) {
            val list = this@HomeViewModel.checkedRecords()
            list.forEach {
                it.state = Record.STATE_WAIT
            }
            insert(list)
            selectedRecords.forEach { it.state = Record.STATE_ORIGIN }
            selectedRecords.clear()
            DataWrap.success("")
        }
    }

    fun parseClipData(context: Context?, clipData: ClipData) = asyncWithWrap<String> {
        val records = ArrayList<Record>()
        val itemCount = clipData.itemCount
        for (i in 0 until itemCount) {
            val item = clipData.getItemAt(i)
            val uri = item.uri
            val path = context?.urlToPath(uri)
            if (null != path) {
                val file = File(path)
                val record = Record(
                    System.currentTimeMillis(),
                    file.length(),
                    file.lastModified(),
                    file.absolutePath,
                    Record.STATE_WAIT
                )
                records.add(record)
            }
        }
        database.recordDao(context) {
            insert(records)
            DataWrap.success("")
        }
    }
}