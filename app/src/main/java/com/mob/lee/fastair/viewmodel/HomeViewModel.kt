package com.mob.lee.fastair.viewmodel

import androidx.lifecycle.MutableLiveData
import java.io.File

class HomeViewModel :AppViewModel(){
    var isDes=true
    val selectedFileLiveData=MutableLiveData<List<File>>()
    val hasSelectedLiveData=MutableLiveData<Boolean>()

    fun parseClip(){

    }

    fun updateLocation(location:Int){

    }

    fun reverse():Boolean{
        return true
    }

    fun select():Boolean{
        return true
    }

    fun deleteSelected(){

    }

    inline fun <R: Comparable<R>> sort(crossinline selector: (File) -> R?) {
        val value = selectedFileLiveData.value
        selectedFileLiveData.value = if (isDes) {
            value?.sortedByDescending(selector)
        } else
            value?.sortedBy(selector)
    }

    fun sortByName(){
        sort { it.name }
    }

    fun sortBySize(){
        sort { it.length() }
    }

    fun sortByDate(){
        sort { it.lastModified() }
    }
}