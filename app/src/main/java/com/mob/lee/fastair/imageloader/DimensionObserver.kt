package com.mob.lee.fastair.imageloader

import android.util.Log
import android.view.ViewTreeObserver
import android.widget.ImageView
import java.lang.ref.WeakReference

/**
 * Created by Andy on 2017/11/12.
 */
class DimensionObserver(view:ImageView,val mOnSizeReady:(Int,Int)->Unit):ViewTreeObserver.OnGlobalLayoutListener{

    val mRef:WeakReference<ImageView>

    init {
        mRef=WeakReference<ImageView>(view)
        val observer = view.viewTreeObserver
        observer.addOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        val imageView = mRef.get()
        imageView?.let {
            mOnSizeReady(imageView.width,imageView.height)
            imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            mRef.clear()
        }
    }

}