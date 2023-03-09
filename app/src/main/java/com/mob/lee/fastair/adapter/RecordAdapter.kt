package com.mob.lee.fastair.adapter

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.imageloader.DisplayManager
import com.mob.lee.fastair.io.state.ProcessState
import com.mob.lee.fastair.io.state.StartRecordState
import com.mob.lee.fastair.io.state.State
import com.mob.lee.fastair.io.state.SuccessState
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.model.formatDate
import com.mob.lee.fastair.utils.dialog
import com.mob.lee.fastair.utils.openFile
import com.mob.lee.fastair.view.CircleProgress
import java.io.File

typealias History = Pair<Record, State>

class RecordAdapter(val action: (Record) -> Unit) : AppListAdapter<History>(R.layout.item_history) {
    override fun onBindViewHolder(holder: AppViewHolder, position: Int, data: History) {

        val preview = holder.view<ImageView>(R.id.item_history_preview)
        val progress = holder.view<CircleProgress>(R.id.item_history_progress)

        val record = data.first
        val state = data.second
        holder.text(R.id.item_history_title, record.name)
        holder.text(R.id.item_history_date, record.date.formatDate("MM/dd/yy HH:mm"))
        when (state) {
            is ProcessState -> {
                progress.progress(state.percentage()*100)
                holder.text(R.id.item_history_speed, "${state.speed()}MB/S")
            }

            is SuccessState -> {
                progress.updateState(CircleProgress.SUCCESS)
                holder.text(R.id.item_history_speed, "${state.speed}MB/S")
            }

            else -> {
                progress.updateState(CircleProgress.FAILED)
            }
        }

        preview.let {
            DisplayManager.show( it,record.path)
        }

        holder.itemView.setOnClickListener {
            if (state is SuccessState) {
                holder.itemView.context.openFile(record.path)
            }
        }
        holder.itemView.setOnLongClickListener {
            if (state is SuccessState) {
                holder.itemView.context.dialog {
                    setTitle(R.string.file_operation)
                            .setItems(R.array.array_file_operation) { _, which ->
                                when (which) {
                                    0 -> fileDetail(it.context, record.path)

                                    1 -> rename(it.context, position, record)
                                }
                            }
                }
                true
            } else {
                false
            }
        }
    }

    fun fileDetail(context: Context, path: String) {
        context.dialog {
            setTitle(path.substringAfterLast(File.separator))
                    .setAdapter(FileDetailAdapter(context, path), null)
        }
    }

    fun rename(context: Context, pos: Int, record: Record) {
        val path = record.path
        val container = LayoutInflater.from(context).inflate(R.layout.item_file_rename, null)
        val et = container.findViewById<EditText>(R.id.item_et_file_rename_content)
        et.setText(record.name.substringBeforeLast("."))
        context.dialog {
            setTitle(path.substringAfterLast(File.separator))
                    .setView(container)
                    .setPositiveButton(R.string.file_rename) { dialog, which ->
                        val input = et.text.toString()
                        val origin = File(path)
                        val target = File("${origin.parent}${File.separator}${input}.${origin.extension}")
                        if (File(path).renameTo(target)) {
                            val rec = record.copy(path = target.absolutePath)
                            update(pos, rec to SuccessState(rec))
                            action(rec)
                        }
                    }
        }
    }

    fun update(record: Record,state:State) {
        val index = itemCount - 1
        if(state is StartRecordState) {
            add(record to state)
        }else{
            update(index,record to state)
        }
    }
}