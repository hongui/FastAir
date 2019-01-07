package com.mob.lee.fastair.adapter

import android.view.View
import android.view.ViewGroup

/**
 * Created by andy on 2018/12/21.
 */
class SimgleDataHolder(val view: View? = null, override val layout: Int = view?.id
        ?: 0) : DataHolder<Int> {

    override val datas: MutableList<Int>?=null

    override var adapter : Adapter?=null

    override fun targetView(parent: ViewGroup?): View? {
        return view ?: super.targetView(parent)
    }

    override var startPosition: Int = 0

    override fun size() = 1

    override fun bind(position: Int, viewHolder: ViewHolder) {

    }
}