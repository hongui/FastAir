package com.mob.lee.fastair.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/11 16:59
 * @Description:    无
 */
open class AppAdapter<D>(val layout:Int) :RecyclerView.Adapter<AppViewHolder>(){
    val datas=ArrayList<D>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder(LayoutInflater.from(parent.context).inflate(layout,parent,false))
    }

    override fun getItemCount()=datas.size

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        onBindViewHolder(holder, position,datas[position])
    }

    open fun onBindViewHolder(holder: AppViewHolder, position: Int, data:D) {

    }

    fun clear(){
        datas.clear()
        notifyDataSetChanged()
    }

    fun add(data:D?){
        data?:return
        datas.add(data)
        notifyItemInserted(datas.size)
    }
}