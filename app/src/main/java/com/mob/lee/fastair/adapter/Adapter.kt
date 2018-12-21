package com.mob.lee.fastair.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * Created by Andy on 2017/8/8.
 */
class Adapter(vararg dataHolder: DataHolder<out Any>) : RecyclerView.Adapter<ViewHolder>() {
    val datas = ArrayList<DataHolder<out Any>>()

    init {
        for (d in dataHolder) {
            add(d)
        }
    }

    override fun getItemViewType(position: Int): Int {
        for ((index, d) in datas.withIndex()) {
            if (d.canHandleIt(position)) {
                return index
            }
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = datas[viewType].targetView(parent)
        view ?: throw NullPointerException("DataType:$viewType has null View!")
        return ViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        datas[holder.dataType].bind(position, holder)
    }

    override fun getItemCount(): Int {
        var total = 0
        for (d in datas) {
            total += d.size()
        }
        return total
    }

    fun add(data: DataHolder<out Any>, position: Int = datas.size) {
        if (position < 0 || position > datas.size) {
            return
        }
        data.startPosition = itemCount
        datas.add(position, data)
        notifyItemRangeInserted(position, data.size())
    }

    fun change(any: Any?) {
        var pos = 0
        var updateCount = 0
        for (d in datas) {
            val flag = d.change(pos - updateCount, any)
            //判断之后才更新起点
            d.startPosition = pos
            handleFlag(flag, pos)
            pos += d.size()
            if (1 == abs(flag)) {
                updateCount += 1
            }
        }
    }

    fun handleFlag(flag: Int, pos: Int) {
        when {
            flag > 0 -> notifyItemInserted(pos)
            flag == -1 -> notifyItemRemoved(pos)
            flag < -1 -> notifyItemChanged(pos)
        }
    }

    fun clearAndAdd(list: List<Any>) {
        clearAll()
        for (l in list) {
            change(l)
        }
    }

    fun clearAll() {
        var pos = 0
        for (d in datas) {
            pos += d.datas?.size ?: 0
            d.datas?.clear()
        }
        notifyItemRangeRemoved(0, pos)
    }
}