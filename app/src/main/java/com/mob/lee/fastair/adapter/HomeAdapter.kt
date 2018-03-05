package com.mob.lee.fastair.adapter

import android.os.Bundle
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppActivity
import com.mob.lee.fastair.fragment.ContentPickFragment
import com.mob.lee.fastair.fragment.DiscoverFragment
import com.mob.lee.fastair.model.FileCategory
import com.mob.lee.fastair.model.IS_CHAT

/**
 * Created by Andy on 2017/6/21.
 */
class HomeAdapter: Adapter<FileCategory>() {
    override fun layout(): Int =R.layout.item_operation

    override fun bind(data: FileCategory, holder: ViewHolder?, position: Int) {
        holder?.drawable(R.id.item_category_icon, data.icon)
        holder?.text(R.id.item_category_title, data.title)
        holder?.itemView?.setOnClickListener {
            val activity=context as AppActivity
            val bundle = Bundle()
            if (position == datas.size - 1) {
                bundle.putBoolean(IS_CHAT, true)
                activity.fragment(DiscoverFragment::class,bundle)
            } else {
                bundle.putInt("fileCategory", data.category)
                activity.fragment(ContentPickFragment::class,bundle)
            }
        }
    }
}