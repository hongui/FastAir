package com.mob.lee.fastair.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/11 16:51
 * @Description:    无
 */
class SingleAdapter(val layout:Int) : RecyclerView.Adapter<AppViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder.create(parent,layout)
    }

    override fun getItemCount()=1

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
    }
}