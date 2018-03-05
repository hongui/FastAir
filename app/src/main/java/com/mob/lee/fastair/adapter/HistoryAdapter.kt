package com.mob.lee.fastair.adapter

import android.widget.ImageView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.model.STATE_SUCCESS
import com.mob.lee.fastair.model.formatDate
import com.mob.lee.fastair.utils.display
import com.mob.lee.fastair.view.CircleProgress

/**
 * Created by Andy on 2017/9/19.
 */
class HistoryAdapter : Adapter<Record>() {
    var mCurrentIndex: Int = -1
    var mCurrentProgress: Int = 0
    var mCurrentState: Int = 0

    override fun layout(): Int = R.layout.item_history

    override fun bind(data: Record, holder: ViewHolder?, position: Int) {
        val preview = holder?.view<ImageView>(R.id.item_history_preview)
        val progress = holder?.view<CircleProgress>(R.id.item_history_progress)

        holder?.text(R.id.item_history_title, data.name)
        holder?.text(R.id.item_history_date, data.date.formatDate("MM/dd/yy HH:mm"))
        if (mCurrentIndex == position) {
            progress?.progress(mCurrentProgress.toFloat())
            if (0 != mCurrentState) {
                progress?.updateState(mCurrentState)
            }
        }
        if (STATE_SUCCESS == data.state) {
            progress?.updateState(CircleProgress.SUCCESS)
        }
        preview?.let {
            display(it.context,data.path,it)
        }
    }

    fun setCurrent(record: Record) {
        mCurrentState=0
        mCurrentProgress=0

        mCurrentIndex = indexOfCurrent(record)
        if (0 > mCurrentIndex) {
            mCurrentIndex = datas.size
            add(record)
        }
        notifyItemChanged(mCurrentIndex)
    }

    fun setProgress(progress: Int) {
        mCurrentProgress = progress
        notifyItemChanged(mCurrentIndex)
    }

    fun setComplete(state: Int) {
        mCurrentState = state
        notifyItemChanged(mCurrentIndex)
    }

    fun indexOfCurrent(current: Record): Int {
        for (file in datas) {
            if (current == file) {
                return datas.indexOf(file)
            }
        }
        return -1
    }
}