package com.mob.lee.fastair.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.repository.RecordRep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.consumeEachIndexed
import kotlinx.coroutines.launch
import java.io.File

/**
 * Created by Andy on 2017/11/13.
 */
class FileViewModel : AppViewModel() {
    val record = MutableLiveData<Record>()
    val state = MutableLiveData<Int>()
    val update = MutableLiveData<Pair<Int,Record?>>()
    val hasSelect=MutableLiveData<Boolean>()
    var position : Int = 0
    var mCurrentChannel:Channel<Record>?=null

    companion object {
        const val STATE_PRE = 0
        const val STATE_START = 1
        const val STATE_FAILED = 2
        const val STATE_SUCCESS = 3
    }

    fun register(channel : Channel<Record>?) {
        channel ?: return
        mCurrentChannel?.close()
        mCurrentChannel=channel
        viewModelScope.launch(Dispatchers.Main) {
            var size = 0
            state.value= STATE_PRE
            channel.consumeEachIndexed {
                if (0 == it.index) {
                    state.value = STATE_START
                }
                size = it.index+1
                record.value = it.value
            }
            channel.invokeOnClose {
                val flag = if (0 == size) {
                    STATE_FAILED
                } else {
                    STATE_SUCCESS
                }
                state.value = flag
                hasSelect.value=RecordRep.selectRecords.isNotEmpty()
            }
        }
    }

    fun update(channel : Channel<Pair<Int,Record?>>?){
        channel?:return
        viewModelScope.launch (Dispatchers.Main){
            channel.consumeEach {
                update.value=it
            }
            hasSelect.value=RecordRep.selectRecords.isNotEmpty()
        }
    }

    fun load(context : Context) {
        register(RecordRep.load(context, position))
    }

    fun sortBy(selector : (Record) -> Comparable<*>) {
        register(RecordRep.sortBy(position, selector))
    }

    fun reverse() {
        register(RecordRep.reverse(position))
    }

    fun updateState(state:Int,start:Int,count:Int=1){
        update(RecordRep.states(position,state,start, count))
    }

    fun toggleState(){
        register(RecordRep.toggleState(position))
    }

    fun delete(context : Context){
        update(RecordRep.delete(context,position))
    }

    fun checkedrecords() : List<Record> {
        return emptyList()
    }
}