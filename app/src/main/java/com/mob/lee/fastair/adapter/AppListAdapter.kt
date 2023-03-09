package com.mob.lee.fastair.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class AppListAdapter<D>(val layout: Int) : RecyclerView.Adapter<AppViewHolder>() {
    val originData = ArrayList<D>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder.create(parent, layout)
    }

    override fun getItemCount() = originData.size


    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        onBindViewHolder(holder, position, originData[position])
    }

    open fun onBindViewHolder(holder: AppViewHolder, position: Int, data: D) {

    }

    fun clear() {
        originData.clear()
        notifyDataSetChanged()
    }

    fun add(data: D?) {
        data ?: return
        if (!originData.contains(data)) {
            originData.add(data)
            notifyItemInserted(originData.size)
        }
    }

    fun add(data: Collection<D>?) {
        data ?: return
        val filter = data.filter { !originData.contains(it) }
        originData.addAll(filter)
        notifyItemRangeInserted(originData.size - filter.size, filter.size)
    }

    fun update(pos:Int,data:D?){
        data?:return
        if(originData.isNullOrEmpty()){
            add(data)
            return
        }
        originData[pos]=data
        notifyItemChanged(pos)
    }
}