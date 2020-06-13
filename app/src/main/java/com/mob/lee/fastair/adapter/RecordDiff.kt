package com.mob.lee.fastair.adapter

import androidx.recyclerview.widget.DiffUtil
import com.mob.lee.fastair.model.Record

class RecordDiff :DiffUtil.ItemCallback<Record>(){
    override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
        return oldItem.state==newItem.state
    }

}