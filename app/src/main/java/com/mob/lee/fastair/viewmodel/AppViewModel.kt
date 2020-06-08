package com.mob.lee.fastair.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mob.lee.fastair.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

open class AppViewModel : ViewModel() {
    val stateLiveData = MutableLiveData<Status>()

    /**
     * 执行耗时任务，不会阻塞UI
     */
    /*fun <D> async(action: suspend () -> DataWrap<D>): LiveData<D> {
        val liveData = MutableLiveData<D>()
        stateLiveData.value = StatusLoading()
        viewModelScope.launch(Dispatchers.Main) {
            val result = action()
            stateLiveData.value = if (result.isSuccess()) {
                liveData.value = result.data
                StatusSuccess()
            } else {
                StatusError(result.msg)
            }
        }
        return liveData
    }*/

    fun <D> async(liveData: MutableLiveData<D>? = null, action: suspend MutableLiveData<D>.() -> Unit): LiveData<D> {
        val targetLiveData = liveData ?: MutableLiveData<D>()
        stateLiveData.value = StatusLoading()
        viewModelScope.launch(Dispatchers.Main) {
            stateLiveData.value = try {
                action(targetLiveData)
                StatusComplete()
            } catch (e: Exception) {
                StatusError(e.message)
            }
        }
        return targetLiveData
    }
}