package com.mob.lee.fastair.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

open class AppListAdapter<D>(val layout:Int,diff: DiffUtil.ItemCallback<D>) :ListAdapter<D, AppViewHolder>(diff){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder.create(parent,layout)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        onBindViewHolder(holder, position,currentList[position])
    }

    open fun onBindViewHolder(holder: AppViewHolder, position: Int, data:D) {

    }

    fun clear(){
        this.submitList(null)
    }

    fun add(data:D?){
        data?:return
        submitList(currentList.toMutableList().apply { add(data) })
    }

    fun add(data:Collection<D>?){
        data?:return
        submitList(currentList.toMutableList().apply { addAll(data) })
    }
}