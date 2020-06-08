package com.mob.lee.fastair.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.repository.StorageDataSource
import kotlinx.coroutines.channels.Channel
import java.io.File

class HomeViewModel :AppViewModel(){
    var isDes=true
    val recordLiveData=MutableLiveData<Record>()
    val hasSelectedLiveData=MutableLiveData<Boolean>()
    var position: Int = 0
    val records = ArrayList<Record>()

    val dataSource by lazy {
        StorageDataSource()
    }

    var currentChannel: Channel<Record>?=null

    fun parseClip(){

    }

    fun updateLocation(context:Context?,location:Int){
        position=location
        fetch(context)
    }

    fun fetch(context: Context?) = async(recordLiveData) {
        recordLiveData.value=null
        currentChannel?.cancel()
        records.clear()

        val channel = dataSource.fetch(context, position)
        currentChannel=channel
        for (r in channel) {
            value=r
            records.add(r)
        }
    }


    inline fun <D:Comparable<D>>sortBy(crossinline selector: (Record) -> D) = async(recordLiveData) {
        if(isDes) {
            records.sortByDescending(selector)
        }else{
            records.sortBy(selector)
        }
        for (r in records) {
            value=r
        }
    }


    fun reverse() = async(recordLiveData) {
        isDes=!isDes
        records.reverse()
        for (r in records) {
            value=r
        }
    }

    fun toggleState(position:Int) {
        val record= records.getOrNull(position)
        record?.state=if(Record.STATE_CHECK==record?.state){
            Record.STATE_ORIGIN
        }else{
            Record.STATE_CHECK
        }
    }

    fun delete(context: Context?) = async(recordLiveData) {
        val checkedRecords = checkedRecords()
        for (r in checkedRecords) {
            val removed = dataSource.delete(context, r)
            if(removed){
                records.remove(r)
            }
        }
    }

    fun selectAll()=async(recordLiveData){
        val hasChecked=checkedRecords().isNotEmpty()
        records.forEach {
            it.state=if(hasChecked){
                Record.STATE_ORIGIN
            }else{
                Record.STATE_CHECK
            }
            value=it
        }
    }

    fun checkedRecords(): List<Record> {
        return records.filter { Record.STATE_CHECK==it.state }
    }

    fun sortByName(){
        sortBy { it.name }
    }

    fun sortBySize(){
        sortBy { it.size }
    }

    fun sortByDate(){
        sortBy { it.date }
    }
}