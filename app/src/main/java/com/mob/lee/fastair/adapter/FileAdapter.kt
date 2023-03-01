package com.mob.lee.fastair.adapter

import android.graphics.Color
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.mob.lee.fastair.R
import com.mob.lee.fastair.imageloader.DisplayManager
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.model.formatDate
import com.mob.lee.fastair.model.formatSize
import com.mob.lee.fastair.utils.dialog
import java.io.File

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/11 16:54
 * @Description:    无
 */
class FileAdapter(val action: (Record)->Unit) : AppListAdapter<Record>(R.layout.item_file) {
    override fun onBindViewHolder(holder: AppViewHolder, position: Int, record: Record) {
        holder.text(R.id.item_file_name, record.name)
        holder.text(R.id.item_file_extra, record.date.formatDate() + "\t\t" + record.size.formatSize(holder.itemView.context))
        val iconView = holder.view<ImageView>(R.id.item_file_icon)
        DisplayManager.show(iconView,record.path)

        holder.itemView.setOnClickListener {
           action(record)
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
        val checked=Record.STATE_ORIGIN != record.state
        holder.check(R.id.item_file_selector, checked)
        val color = if (checked) {
            ContextCompat.getColor(holder.itemView.context, R.color.material_grey_300)
        } else {
            Color.WHITE
        }
        holder.itemView.setBackgroundColor(color)
    }
}