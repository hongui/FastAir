package com.mob.lee.fastair.imageloader

import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class DisplayManager(val fragment: Fragment) {
    val tasks = HashMap<String, Deferred<Unit>>()

    val mCache = object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 8 / 1024).toInt()) {
        override fun sizeOf(key: String?, value: Bitmap?): Int {
            if (null == value) {
                return 0
            }
            /*保持大小一致*/
            return value.rowBytes * value.height / 1024
        }
    }

    fun bindRecyclerView(recyclerView: RecyclerView) {
        var canSubmit = true
        val scroller = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                canSubmit = newState == RecyclerView.SCROLL_STATE_IDLE
                Log.e("TAG","======${newState}======")
            }
        }

        val state = object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                if (canSubmit) {
                    Log.e("TAG","============")
                    submit(view)
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                cancel(view)
            }
        }
        recyclerView.addOnScrollListener(scroller)
        recyclerView.addOnChildAttachStateChangeListener(state)
    }

    fun show(view: View, path: String) {
        view.setTag(path)
    }

    fun submit(view: View) {
        val path = view.tag?.let { it as String } ?: return

        val job = fragment.lifecycleScope.async(Dispatchers.IO) {
            decode(view)
        }
        tasks.put(path, job)
    }

    fun cancel(view: View) {
        view.tag?.let {
            val key = it as String?
            key ?: return
            tasks.get(key)?.cancel()
            tasks.remove(key)
        }
    }

    suspend fun decode(view: View) {
        val path = view.getTag(PATH)?.let { it as String } ?: return
        val imageView = (view as ImageView?) ?: return
        val bitmap = mCache.get(path)
        if (null != bitmap) {
            imageView.setImageBitmap(bitmap)
            return
        }
        Decoder.decode(fragment.context, path, imageView) {
            it?.let {
                imageView.setImageBitmap(it)
            }
        }
    }

    companion object {
        const val VERSION = 9527
        const val PATH = VERSION + 1
    }
}