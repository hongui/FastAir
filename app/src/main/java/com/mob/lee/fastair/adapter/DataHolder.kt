package com.mob.lee.fastair.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

interface DataHolder<D>{
    val layout:Int
    var startPosition:Int
    val datas:MutableList<D>?

    fun targetView(parent: ViewGroup?): View?{
        return LayoutInflater.from(parent?.context)?.inflate(layout,parent,false)
    }

    fun canHandleIt(position:Int):Boolean=startPosition<=position && position<startPosition+size()

    fun size():Int

    fun bind(position:Int,viewHolder:ViewHolder)

    fun change(pos:Int,data: Any?):Int=-1
}