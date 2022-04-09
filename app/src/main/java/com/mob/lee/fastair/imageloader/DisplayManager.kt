package com.mob.lee.fastair.imageloader

import android.graphics.Bitmap
import android.util.LruCache
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

object DisplayManager {
    val mCache =
        object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 8 / 1024).toInt()) {
            override fun sizeOf(key: String?, value: Bitmap?): Int {
                if (null == value) {
                    return 0
                }
                /*保持大小一致*/
                return value.rowBytes * value.height / 1024
            }
        }

    fun show(view: ImageView, path: String) {
        mCache.get(path)?.let {
            view.setImageBitmap(it)
            return
        }

        view.tag = path
        val activity = view.context as AppCompatActivity
        activity.lifecycleScope.async(Dispatchers.IO) {
            val dimension = DimensionObserver(this, view)
            val size = dimension.receive()
            val p = view.tag as String
            val bitmap = Decoder.decode(activity, p, size)
            bitmap?.let { b->
                mCache.put(p, b)
                withContext(Dispatchers.Main) {
                    if (view.tag == path) {
                        view.setImageBitmap(b)
                    }
                }
            } ?: Unit
        }
    }
}