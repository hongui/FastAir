package com.mob.lee.fastair.adapter

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mob.lee.fastair.imageloader.ImageLoader

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/11 16:47
 * @Description:    无
 */
class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val views = SparseArray<View>()

    fun text(@IdRes id: Int, @StringRes res: Int): AppViewHolder {
        val view = view<TextView>(id)
        view.setText(res)
        return this
    }

    fun image(@IdRes id: Int, @DrawableRes drawable: Int): AppViewHolder {
        val view = view<ImageView>(id)
        ImageLoader.display(drawable, view)
        return this
    }

    fun image(@IdRes id: Int, drawable: String): AppViewHolder {
        val view = view<ImageView>(id)
        ImageLoader.display(drawable, view)
        return this
    }

    fun text(@IdRes id: Int, charSequence: CharSequence?): AppViewHolder {
        val view = view<TextView>(id)
        view.text = charSequence
        return this
    }

    fun enable(@IdRes id: Int, able: Boolean): AppViewHolder {
        val view = view<TextView>(id)
        view.isEnabled = able
        return this
    }

    fun textColor(@IdRes id: Int, @ColorRes colorRes: Int): AppViewHolder {
        val view = view<TextView>(id)
        view.setTextColor(ContextCompat.getColor(view.context, colorRes))
        return this
    }

    fun listener(@IdRes id: Int, listener: (View) -> Unit): AppViewHolder {
        val view = view<TextView>(id)
        view.setOnClickListener(listener)
        return this
    }

    fun visible(@IdRes id: Int, visible: Int): AppViewHolder {
        val view = view<View>(id)
        view.visibility = visible
        return this
    }

    fun select(@IdRes id: Int, selected: Boolean): AppViewHolder {
        val view = view<View>(id)
        view.isSelected = selected
        return this
    }

    fun check(id: Int, checked: Boolean) {
        view<CheckBox>(id)?.isChecked=checked
    }

    fun <V : View> view(@IdRes id: Int): V {
        var v: View? = views.get(id)
        if (null == v) {
            v = itemView.findViewById(id)
            views.put(id, v)
        }
        return v as V
    }

    companion object{
        fun create(parent: ViewGroup, layout: Int)=AppViewHolder(LayoutInflater.from(parent.context).inflate(layout,parent,false))
    }
}