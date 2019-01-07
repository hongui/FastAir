package com.mob.lee.fastair.adapter

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.model.formatDate
import com.mob.lee.fastair.model.formatSize
import java.io.File

class FileDetailAdapter(context : Context, path : String) : BaseAdapter() {
    val infos : SparseArray<String>

    init {
        val file = File(path)
        infos = SparseArray<String>()

        infos.append(R.string.file_size, file.length().formatSize(context))
        infos.append(R.string.file_type, path.substringAfterLast(".", "*"))
        infos.append(R.string.file_date, file.lastModified().formatDate("yyyy年MM月dd日 HH时mm分ss秒"))
        infos.append(R.string.file_path, path)
    }

    override fun getView(position : Int, convertView : View?, parent : ViewGroup?) : View {
        var container = if (null == convertView) {
            LayoutInflater.from(parent?.context)
                    .inflate(R.layout.item_file_detail, parent, false)
        } else {
            convertView
        }
        val key = infos.keyAt(position)
        val item = infos.get(key)

        val label = container.findViewById<TextView>(R.id.item_tv_file_detail_label)
        val value = container.findViewById<TextView>(R.id.item_tv_file_detail_value)

        label.setText("${container.context.getString(key)}：")
        value.setText(item)

        return container
    }

    override fun getItem(position : Int) = infos.valueAt(position)

    override fun getItemId(position : Int) = position.toLong()

    override fun getCount() = infos.size()

}