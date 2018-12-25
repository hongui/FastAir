package com.mob.lee.fastair.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

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

    fun addSingle(data: DataHolder<out Any>, position: Int = datas.size) {
        for (d in datas) {
            if (d.layout == data.layout) {
                return
            }
        }
        add(data, position)
    }

    fun remove(data: DataHolder<out Any>? = null, position: Int = datas.indexOf(data)) {
        if (position >= 0) {
            val holder = datas.removeAt(position)
            notifyItemRangeRemoved(holder.startPosition, holder.size())
        }
    }

    fun remove(layout: Int = -1, count: Int = -1) {
        val it = datas.iterator()
        var tempCount = 0
        while (it.hasNext()) {
            val d = it.next()
            if (-1 != layout && layout != d.layout) {
                continue
            }
            if (-1 != count && tempCount == count) {
                return
            }
            it.remove()
            notifyItemRangeRemoved(d.startPosition, d.size())
            tempCount += 1
        }
    }

    fun change(any: Any?, index: Int = -1, layout: Int = -1) {
        var position = 0
        var updateCount = 0
        var origin = 0
        for (d in datas) {
            origin = d.size()
            if (d.layout != layout && -1 != layout) {
                continue
            }
            val temp = position - updateCount + if (-1 == index) {
                d.size()
            } else {
                index
            }
            val i = d.change(temp, any)
            //判断之后才更新起点
            if (-1 == i) {
                continue
            }else {
                val pos = i + position
                when {
                    origin > d.size() -> {
                        notifyItemRemoved(pos)
                        updateCount -= 1
                    }
                    origin == d.size() -> notifyItemChanged(pos)
                    origin < d.size() -> {
                        notifyItemInserted(pos)
                        updateCount += 1
                    }
                }
            }
            d.startPosition = position
            position += d.size()
        }
    }

    fun clearAndAdd(list: List<Any>) {
        clearAll()
        for ((i, d) in list.withIndex()) {
            change(d, i)
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