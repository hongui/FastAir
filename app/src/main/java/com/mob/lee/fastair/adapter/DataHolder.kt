package com.mob.lee.fastair.adapter

/**
 * Created by andyt on 2017/6/7.
 */
abstract class DataHolder<D>{
    var mDatas=ArrayList<D>()
    abstract fun layout():Int

    open fun handleIt(position:Int)=true

    open fun size()=mDatas.size

    open fun bind(position:Int,viewHolder:ViewHolder){}
}