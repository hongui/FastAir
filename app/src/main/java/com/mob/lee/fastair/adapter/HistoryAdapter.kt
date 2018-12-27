package com.mob.lee.fastair.adapter

import android.widget.ImageView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.io.state.FaildState
import com.mob.lee.fastair.io.state.ProcessState
import com.mob.lee.fastair.io.state.State
import com.mob.lee.fastair.io.state.SuccessState
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.model.formatDate
import com.mob.lee.fastair.utils.display
import com.mob.lee.fastair.view.CircleProgress

/**
 * Created by Andy on 2017/9/19.
 */
typealias History = Pair<Record,State>

class HistoryAdapter : MultiDataHolder<History>(R.layout.item_history) {

    override fun bind(position: Int, holder: ViewHolder) {
        val originPos=position-startPosition
        val data=datas.getOrNull(originPos)
        data?:return

        val preview = holder.view<ImageView>(R.id.item_history_preview)
        val progress = holder.view<CircleProgress>(R.id.item_history_progress)

        val record=data.first
        val state=data.second
        holder.text(R.id.item_history_title, record.name)
        holder.text(R.id.item_history_date, record.date.formatDate("MM/dd/yy HH:mm"))
        when(state){
            is ProcessState-> {
                progress?.progress(state.percentage())
            }

            is SuccessState->{
                progress?.updateState(CircleProgress.SUCCESS)
            }

            is FaildState->{
                progress?.updateState(CircleProgress.FAILED)
            }
        }

        preview?.let {
            display(it.context, record.path, it)
        }
    }
}