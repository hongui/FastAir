package com.mob.lee.fastair.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mob.lee.fastair.ContainerActivity
import com.mob.lee.fastair.model.*
import kotlinx.coroutines.launch

open class AppViewModel : ViewModel() {
    val stateLiveData = MutableLiveData<Status>()

    /**
     * 执行耗时任务，不会阻塞UI
     */
    fun <D> async(action: suspend () -> DataWrap<D>): LiveData<D> {
        val liveData = MutableLiveData<D>()
        stateLiveData.value = StatusLoading()
        viewModelScope.launch {
            val result = action()
            stateLiveData.value = if (result.isSuccess()) {
                liveData.value = result.data
                StatusSuccess()
            } else {
                StatusError(result.msg)
            }
        }
        return liveData
    }
}