package com.mob.lee.fastair.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mob.lee.fastair.R

/**
 * Created by Andy on 2017/8/8.
 */
abstract class Adapter<D> : RecyclerView.Adapter<ViewHolder>() {
    var hasEmpty: Boolean = true
    var context: Context? = null
    val datas = ArrayList<D>()

    abstract fun layout(): Int

    abstract fun bind(data: D, holder: ViewHolder?, position: Int)

    override fun getItemViewType(position: Int)=if(hasEmpty)0 else 1

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        context = parent?.context
        if (0==viewType) {
            return ViewHolder(LayoutInflater.from(context)?.inflate(R.layout.empty, parent, false))
        }
        return ViewHolder(LayoutInflater.from(context)?.inflate(layout(), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (datas.isEmpty() || position >= datas.size) {
            return
        }
        bind(datas[position], holder, position)
    }

    override fun getItemCount(): Int = if (hasEmpty) 1 else datas.size

    fun addAll(newData: List<D>): Adapter<D> {
        handleEmpty(newData.isEmpty())
        val size = datas.size
        datas.addAll(newData)
        notifyItemRangeInserted(size, newData.size)
        return this
    }

    fun add(data: D, position: Int = datas.size): Adapter<D> {
        handleEmpty(false)
        datas.add(position, data)
        notifyItemInserted(position)
        return this
    }

    fun remove(position: Int) {
        if (0 > position || position >= datas.size) {
            return
        }
        datas.removeAt(position)
        notifyItemRemoved(position)
    }

    fun remove(data: D){
        val index=datas.indexOf(data)
        if (0 > index || index >= datas.size) {
            return
        }
        datas.removeAt(index)
        notifyItemRemoved(index)
    }

    fun clearAndAdd(newData: List<D>) {
        if(datas.isNotEmpty()) {
            notifyItemRangeRemoved(0, datas.size)
            datas.clear()
        }
        datas.addAll(newData)
        notifyItemRangeInserted(0, datas.size)
    }

    fun clearAll(){
        if(datas.isNotEmpty()){
            notifyItemRangeRemoved(0,datas.size)
            datas.clear()
        }
    }

    fun handleEmpty(isEmpty:Boolean=true){
        if (hasEmpty&&!isEmpty) {
            hasEmpty = false
            notifyDataSetChanged()
        }
    }

}