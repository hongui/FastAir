package com.mob.lee.fastair.adapter

import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView

/**
 * Created by Andy on 2017/6/7.
 */
class ViewHolder(view:View?):RecyclerView.ViewHolder(view){

    val mViews:SparseArray<View> =SparseArray()

    fun <V:View> view(id:Int):V?{
        var view = mViews.get(id)
        if (null == view) {
            view=itemView.findViewById(id)
            mViews.put(id,view)
        }
        if(null==view){
            return null
        }
        return view as V
    }

    fun text(id:Int,resId: Int){
        view<AppCompatTextView>(id)?.setText(resId)
    }

    fun text(id:Int,content:CharSequence){
        view<AppCompatTextView>(id)?.text=content
    }

    fun drawable(id: Int, resId: Int) {
        view<ImageView>(id)?.setImageResource(resId)
    }

    fun check(id: Int, checked: Boolean) {
        view<CheckBox>(id)?.isChecked=checked
    }

    fun click(id:Int,click:View.OnClickListener){
        view<View>(id)?.setOnClickListener(click)
    }
}