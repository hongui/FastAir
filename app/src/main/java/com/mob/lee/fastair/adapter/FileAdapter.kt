package com.mob.lee.fastair.adapter

import android.graphics.Color
import android.widget.CheckBox
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.mob.lee.fastair.R
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.model.formatDate
import com.mob.lee.fastair.model.formatSize
import com.mob.lee.fastair.utils.dialog
import com.mob.lee.fastair.utils.display
import com.mob.lee.fastair.viewmodel.HomeViewModel
import java.io.File

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/11 16:54
 * @Description:    无
 */
class FileAdapter(val viewModel: HomeViewModel?=null) : AppListAdapter<Record>(R.layout.item_file,RecordDiff()) {
    override fun onBindViewHolder(holder: AppViewHolder, position: Int, record: Record) {
        holder.text(R.id.item_file_name, record.name)
        holder.text(R.id.item_file_extra, record.date.formatDate() + "\t\t" + record.size.formatSize(holder.itemView.context))
        val iconView = holder.view<ImageView>(R.id.item_file_icon)
        if (null != iconView) {
            display(holder.itemView.context, record.path, iconView)
        }

        holder.itemView.setOnClickListener {
            viewModel?.toggleState(record)
            notifyItemChanged(position)
        }
        holder.itemView.setOnLongClickListener {
            val context = holder.itemView.context
            context.dialog {
                setTitle(record.path.substringAfterLast(File.separator))
                        .setAdapter(FileDetailAdapter(context, record.path), null)
            }
            true
        }
        holder.check(R.id.item_file_selector, Record.STATE_CHECK == record.state)
        val isChecked = holder.view<CheckBox>(R.id.item_file_selector)?.isChecked ?: false
        val color = if (isChecked) {
            ContextCompat.getColor(holder.itemView.context, R.color.material_grey_300)
        } else {
            Color.WHITE
        }
        holder.itemView.setBackgroundColor(color)
    }
}