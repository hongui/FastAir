package com.mob.lee.fastair.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.io.state.FaildState
import com.mob.lee.fastair.io.state.ProcessState
import com.mob.lee.fastair.io.state.State
import com.mob.lee.fastair.io.state.SuccessState
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.model.formatDate
import com.mob.lee.fastair.utils.database
import com.mob.lee.fastair.utils.dialog
import com.mob.lee.fastair.utils.display
import com.mob.lee.fastair.utils.openFile
import com.mob.lee.fastair.utils.updateStorage
import com.mob.lee.fastair.view.CircleProgress
import kotlinx.coroutines.GlobalScope
import java.io.File

/**
 * Created by Andy on 2017/9/19.
 */
typealias History = Pair<Record, State>

class HistoryAdapter : MultiDataHolder<History>(R.layout.item_history) {

    override fun bind(position : Int, holder : ViewHolder) {
        val originPos = position - startPosition
        val data = datas.getOrNull(originPos)
        data ?: return

        val preview = holder.view<ImageView>(R.id.item_history_preview)
        val progress = holder.view<CircleProgress>(R.id.item_history_progress)

        val record = data.first
        val state = data.second
        holder.text(R.id.item_history_title, record.name)
        holder.text(R.id.item_history_date, record.date.formatDate("MM/dd/yy HH:mm"))
        when (state) {
            is ProcessState -> {
                progress?.progress(state.percentage())
            }

            is SuccessState -> {
                progress?.updateState(CircleProgress.SUCCESS)
            }

            is FaildState -> {
                progress?.updateState(CircleProgress.FAILED)
            }
        }

        preview?.let {
            display(it.context, record.path, it)
        }

        holder.itemView.setOnClickListener {
            if (state is SuccessState) {
                holder.itemView.context.openFile(record.path)
            }
        }
        holder.itemView.setOnLongClickListener {
            if (state is SuccessState) {
                holder.itemView.context.dialog {
                    it.setTitle(R.string.file_operation)
                            .setItems(R.array.array_file_operation) { dialog, which ->
                                when (which) {
                                    0 -> {
                                        fileDetail(it.context, record.path)
                                    }

                                    1 -> {
                                        rename(it.context, position, record)
                                    }
                                }
                            }
                }
                true
            } else {
                false
            }
        }
    }

    fun fileDetail(context : Context, path : String) {
        context.dialog {
            it.setTitle(path.substringAfterLast(File.separator))
                    .setAdapter(FileDetailAdapter(context, path), null)
        }
    }

    fun rename(context : Context, pos : Int, record : Record) {
        val path = record.path
        val container = LayoutInflater.from(context).inflate(R.layout.item_file_rename, null)
        val et = container.findViewById<EditText>(R.id.item_et_file_rename_content)
        et.setText(record.name.substringBeforeLast("."))
        context.dialog {
            it.setTitle(path.substringAfterLast(File.separator))
                    .setView(container)
                    .setPositiveButton(R.string.file_rename) { dialog, which ->
                        val input = et.text.toString()
                        val origin = File(path)
                        val target = File("${origin.parent}${File.separator}${input}.${origin.extension}")
                        if (File(path).renameTo(target)) {
                            val rec = record.copy(path = target.absolutePath)
                            adapter?.change(rec to SuccessState(),pos)
                            context.updateStorage(target.absolutePath)

                            context.database(GlobalScope) { dao ->
                                dao.update(rec)
                            }
                        }
                    }
        }
    }
}