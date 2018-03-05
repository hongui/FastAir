package com.mob.lee.fastair.imageloader

import android.view.ViewTreeObserver
import android.widget.ImageView
import java.lang.ref.WeakReference

/**
 * Created by Andy on 2017/11/12.
 */
class DimensionObserver(view:ImageView,val mOnSizeReady:(Int,Int)->Unit):ViewTreeObserver.OnPreDrawListener{

    val mRef:WeakReference<ImageView>

    init {
        mRef=WeakReference<ImageView>(view)
        val observer = view.viewTreeObserver
        observer.addOnPreDrawListener(this)
    }

    override fun onPreDraw(): Boolean {
        val imageView = mRef.get()
        imageView?.let {
            mOnSizeReady(imageView.width,imageView.height)
            imageView.viewTreeObserver.removeOnPreDrawListener(this)
            mRef.clear()
        }
        return true
    }

}