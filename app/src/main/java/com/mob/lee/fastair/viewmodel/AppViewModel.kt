package com.mob.lee.fastair.viewmodel

import androidx.lifecycle.*
import com.mob.lee.fastair.model.DataLoad
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class AppViewModel : ViewModel() {
    val stateLiveData = DataLoad<Any>()

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

    fun <D> async(liveData: MutableLiveData<D>? = null, action: suspend DataLoad<D>.() -> Unit): LiveData<D> {
        val targetLiveData = liveData ?: MutableLiveData<D>()
        targetLiveData.value = null

        val dataLoad = DataLoad<D>()

        val observer = Observer<Pair<Int, D?>> {
            stateLiveData.value = it
            if (it.first == DataLoad.NEXT) {
                targetLiveData.value = it?.second
            }
        }

        dataLoad.observeForever(observer)

        viewModelScope.launch(Dispatchers.Main) {
            try {
                action(dataLoad)
                when (dataLoad.code) {
                    DataLoad.ERROR -> dataLoad.error(null)
                    DataLoad.LOADING -> dataLoad.empty()
                    DataLoad.NEXT -> dataLoad.complete()
                }

                dataLoad.removeObserver(observer)
            } catch (e: Exception) {
                dataLoad.error(e.message)
            } finally {
                stateLiveData.value = null
            }
        }
        return targetLiveData
    }
}