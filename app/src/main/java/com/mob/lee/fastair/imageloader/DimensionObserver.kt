package com.mob.lee.fastair.imageloader

import android.util.Log
import android.util.Size
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 * Created by Andy on 2017/11/12.
 */
class DimensionObserver(val scope:CoroutineScope,view:View):ViewTreeObserver.OnPreDrawListener{

    val mRef:WeakReference<View>
    val channel=Channel<Size>()

    init {
        mRef=WeakReference<View>(view)
        val observer = view.viewTreeObserver
        observer.addOnPreDrawListener(this)
    }

    override fun onPreDraw(): Boolean {
        val view = mRef.get()
        view?.let {v->
            scope.launch {
                channel.send(Size(v.width , v.height))
            }
            view.viewTreeObserver.removeOnPreDrawListener(this)
            mRef.clear()
        }
        return false
    }

    suspend fun receive():Size{
        val value=channel.receive()
        channel.close()
        return value
    }

}