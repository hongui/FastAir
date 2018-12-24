package com.mob.lee.fastair.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mob.lee.fastair.base.AndroidScope
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.repository.RecordRep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.consumeEachIndexed
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

/**
 * Created by Andy on 2017/11/13.
 */
class FileViewModel : ViewModel() {
    val record = MutableLiveData<Record>()
    val state = MutableLiveData<Int>()
    val update = MutableLiveData<Record>()
    var mCheckAll = false
    var mIsDes = true
    var position : Int by Delegates.observable(0, { property, oldValue, newValue ->
        state.value = STATE_PRE
    })
    var mCurrentChannel:Channel<Record>?=null

    companion object {
        const val STATE_PRE = 0
        const val STATE_START = 1
        const val STATE_FAILED = 2
        const val STATE_SUCCESS = 3
    }

    fun register(scope : AndroidScope, channel : Channel<Record>?) {
        channel ?: return
        mCurrentChannel?.close()
        mCurrentChannel=channel
        scope.launch(Dispatchers.Main) {
            var size = 0
            state.value= STATE_PRE
            channel.consumeEachIndexed {
                if (0 == it.index) {
                    state.value = STATE_START
                }
                size = it.index
                record.value = it.value
            }
            channel.invokeOnClose {
                val flag = if (0 == size) {
                    STATE_FAILED
                } else {
                    STATE_SUCCESS
                }
                state.value = flag
            }
        }
    }

    fun update(scope : AndroidScope, channel : Channel<Record>?){
        channel?:return
        scope.launch (Dispatchers.Main){
            channel.consumeEach {
                update.value=it
            }
        }
    }

    fun load(scope : AndroidScope, context : Context) {
        register(scope, RecordRep.load(context, position))
    }

    fun sortBy(scope : AndroidScope, selector : (Record) -> Comparable<*>) {
        register(scope, RecordRep.sortBy(position, selector))
    }

    fun reverse(scope : AndroidScope) {
        register(scope, RecordRep.reverse(position))
    }

    fun updateState(scope : AndroidScope,state:Int,start:Int,count:Int=1){
        update(scope, RecordRep.states(position,state,start, count))
    }

    fun toggleState(scope : AndroidScope){
        register(scope, RecordRep.toggleState(position))
    }

    fun delete(context : Context,scope : AndroidScope){
        update(scope, RecordRep.delete(context,position))
    }

    fun checkedrecords() : List<Record> {
        return emptyList()
    }
}