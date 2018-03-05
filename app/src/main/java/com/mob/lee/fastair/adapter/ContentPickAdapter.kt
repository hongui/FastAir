package com.mob.lee.fastair.adapter

import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.widget.CheckBox
import android.widget.ImageView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.model.STATE_CHECK
import com.mob.lee.fastair.model.STATE_ORIGIN
import com.mob.lee.fastair.model.formatDate
import com.mob.lee.fastair.model.formatSize
import com.mob.lee.fastair.utils.display

/**
 * Created by Andy on 2017/6/20.
 */
class ContentPickAdapter() : Adapter<Record>() {

    override fun layout(): Int = R.layout.item_file

    override fun bind(record: Record, holder: ViewHolder?, position: Int) {
        holder?.text(R.id.item_file_name, record.name)
        holder?.text(R.id.item_file_extra, record.date.formatDate() + "\t\t" + record.size.formatSize(holder.itemView.context))
        val iconView = holder?.view<ImageView>(R.id.item_file_icon)
        val checkBox = holder?.view<CheckBox>(R.id.item_file_selector)
        if (null != iconView) {
            display(holder.itemView.context, record.path, iconView)
        }

        holder?.itemView?.setOnClickListener {
            checkBox?.isChecked = STATE_CHECK == record.state
            if (checkBox?.isChecked?:false) {
                record.state = STATE_ORIGIN
            } else {
                record.state = STATE_CHECK
            }
            notifyItemChanged(position)
        }
        holder?.check(R.id.item_file_selector, STATE_CHECK== record.state)
        val isChecked=holder?.view<CheckBox>(R.id.item_file_selector)?.isChecked?:false
        val color=if(isChecked){
            ContextCompat.getColor(context,R.color.material_grey_300)
        }else{
            Color.WHITE
        }
        holder?.itemView?.setBackgroundColor(color)
    }

    fun selectOrUnSelectAll(isAll:Boolean) {
        datas.forEach {
            if (isAll) {
                it.state = STATE_ORIGIN
            } else {
                it.state = STATE_CHECK
            }
        }
        notifyItemRangeChanged(0, datas.size)
    }
}