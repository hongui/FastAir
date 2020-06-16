package com.mob.lee.fastair.viewmodel

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.MutableLiveData
import com.mob.lee.fastair.R
import com.mob.lee.fastair.model.DataLoad
import com.mob.lee.fastair.model.DataWrap
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.repository.DataBaseDataSource
import com.mob.lee.fastair.repository.StorageDataSource
import com.mob.lee.fastair.utils.dialog
import kotlinx.coroutines.channels.Channel

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
        if (position != location) {
            position = location
            withPermission(fragment, Manifest.permission.WRITE_EXTERNAL_STORAGE, action = { _, hasPermission ->
                if (hasPermission) {
                    fetch(fragment.context)
                } else {
                    fragment.mParent?.dialog {
                        setMessage(R.string.no_permission_to_scan)
                                .setPositiveButton(R.string.turn_on) { _, _ ->
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts("package", fragment.context?.packageName, null)
                                    intent.data = uri
                                    fragment.startActivityForResult(intent, 123)
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

        val channel = dataSource.fetch(context, position)
        currentChannel = channel
        for (r in channel) {
            next(r)
            records.add(r)
        }
    }


    inline fun <D : Comparable<D>> sortBy(crossinline selector: (Record) -> D) = async(recordLiveData) {
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

    fun update()=async(recordLiveData){
        records.forEach { next(it) }
    }

    fun selectAll() = async(recordLiveData) {
        val hasChecked = checkedRecords().isNotEmpty()
        records.forEach {
            it.state = if (hasChecked) {
                Record.STATE_ORIGIN
            } else {
                Record.STATE_CHECK
            }
            next(it)
        }
    }

    fun toggleState(record: Record){
        record.state=if(Record.STATE_CHECK==record.state){
            selectedRecords.remove(record)
            Record.STATE_ORIGIN
        }else{
            selectedRecords.add(record)
            Record.STATE_CHECK
        }
        hasSelectedLiveData.value=selectedRecords.isNotEmpty()
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

    fun write(context: Context?)=asyncWithWrap<String>{

        database.recordDao(context){
            val list=checkedRecords()
            if(list.isEmpty()){
                DataWrap.error(context?.getString(R.string.tip_have_no_file))
            }else {
                insert(list)
                DataWrap.success("")
            }
        }
    }
}