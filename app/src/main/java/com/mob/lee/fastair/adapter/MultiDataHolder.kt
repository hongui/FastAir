package com.mob.lee.fastair.adapter

open class MultiDataHolder<D>(override val layout: Int, val bindAction: ((position: Int, data: D?, viewHolder: ViewHolder) -> Unit)? = null) : DataHolder<D> {

    override val datas = ArrayList<D>()

    override var startPosition: Int = 0

    override fun size() = datas.size

    override fun bind(position: Int, viewHolder: ViewHolder) {
        val originPos = position - startPosition
        val data = datas.getOrNull(originPos)
        bindAction?.invoke(position, data, viewHolder)
    }

    override fun change(pos: Int, data: Any?): Int {
        if (startPosition == pos && null != data) {
            for ((i, d) in datas.withIndex()) {
                //引用一样，说明要更新或者删除
                if (d === data) {
                    //完全相等，删除
                    if (d == data) {
                        datas.removeAt(i)
                    } else {
                        datas[i] = data
                    }
                    return i
                }
            }
        }
        //没找到，加进去吧
        data?.let {
            val real = data as? D
            real?.let {
                datas.add(it)
                return datas.size
            }
        }
        return super.change(pos, data)
    }
}