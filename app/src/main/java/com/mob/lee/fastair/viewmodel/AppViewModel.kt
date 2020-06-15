package com.mob.lee.fastair.viewmodel

import androidx.lifecycle.*
import com.mob.lee.fastair.model.DataLoad
import com.mob.lee.fastair.model.DataWrap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        val targetLiveData = liveData.apply { this?.value=null } ?: MutableLiveData<D>()

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
                val job=launch(Dispatchers.IO) { action(dataLoad) }
                job.join()
                withContext(this.coroutineContext){
                    when (dataLoad.code) {
                        DataLoad.LOADING -> dataLoad.empty()
                        DataLoad.NEXT -> dataLoad.complete()
                    }
                    dataLoad.removeObserver(observer)
                }
            } catch (e: Exception) {
                withContext(coroutineContext) {
                    dataLoad.error(e.message)
                }
            } finally {
                stateLiveData.value = null
            }
        }
        return targetLiveData
    }

    fun <D> asyncWithWrap(liveData: MutableLiveData<DataWrap<D>>? = null, action: suspend () -> DataWrap<D>): LiveData<DataWrap<D>> =async(liveData){
        val data = action()
        next(data)
    }
}