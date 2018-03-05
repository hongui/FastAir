package com.mob.lee.fastair.utils

import android.content.Context
import android.widget.ImageView
import com.mob.lee.fastair.imageloader.ImageLoader
import com.mob.lee.fastair.model.Record

/**
 * Created by Andy on 2017/6/20.
 */
fun display(context: Context, path: String, view: ImageView) {
    ImageLoader.with(context).display(path,view)
}

fun display(context: Context,res:Int, view: ImageView) {
    ImageLoader.with(context).display(res,view)
}