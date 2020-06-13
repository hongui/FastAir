package com.mob.lee.fastair.adapter

import android.widget.CheckedTextView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.viewmodel.HomeViewModel

class DeleteAdapter(val viewModel: HomeViewModel) : AppListAdapter<Record>(R.layout.item_delete, RecordDiff()) {

    init {
        add(viewModel.checkedRecords())
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val record = currentList[position]
        holder.itemView.setOnClickListener {
            viewModel.toggleState(record)
            notifyItemChanged(position)
        }
        val tv = holder.view<CheckedTextView>(R.id.tvDelete)
        tv.text = record.path
        tv.isChecked = Record.STATE_CHECK == record.state
    }
}