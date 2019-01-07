package com.mob.lee.fastair.adapter

open class MultiDataHolder<D>(override val layout: Int, val bindAction: ((position: Int, data: D?, viewHolder: ViewHolder) -> Unit)? = null) : DataHolder<D> {

    override val datas = ArrayList<D>()

    override var startPosition: Int = 0

    override fun size() = datas.size

    override var adapter : Adapter?=null

    override fun bind(position: Int, viewHolder: ViewHolder) {
        val originPos = position - startPosition
        val data = datas.getOrNull(originPos)
        bindAction?.invoke(position, data, viewHolder)
    }

    override fun change(pos: Int, data: Any?): Int {
        val target = data as? D
        if (pos in startPosition until startPosition + size() && (null != target || null == data)) {
            val origin = pos - startPosition
            if (null == data) {
                val temp = datas.removeAt(origin)
                temp?.let {
                    return origin
                }
                return -1
            } else {
                target?.let {
                    datas[origin] = it
                    return origin
                }
                return -1
            }
        }
        //没找到，加进去吧
        target?.let {
            datas.add(it)
            return datas.size
        }
        return super.change(pos, data)
    }
}