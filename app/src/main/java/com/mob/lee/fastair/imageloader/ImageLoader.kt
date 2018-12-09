package com.mob.lee.fastair.imageloader

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import java.io.File
import java.util.concurrent.Executors
import kotlin.properties.Delegates

/**
 * Created by Andy on 2017/11/6.
 */
object ImageLoader {
    val TAG = "ImageLoader"
    var mContext: Context by Delegates.notNull<Context>()
    val mCache = object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 8 / 1024).toInt()) {
        override fun sizeOf(key: String?, value: Bitmap?): Int {
            if (null == value) {
                return 0
            }
            /*保持大小一致*/
            return value.rowBytes * value.height / 1024
        }
    }

    val mExecutor = Executors.newCachedThreadPool()

    fun with(context: Context): ImageLoader {
        mContext = context.applicationContext
        return this
    }

    fun display(path: String, imageView: ImageView?) {
        if (null == imageView) {
            return
        }
        val bitmap = mCache.get(path)
        if (null != bitmap) {
            imageView.setImageBitmap(bitmap)
            return
        }
        imageView.setTag(path)
        mExecutor.submit {
            doDecode(path, imageView)
        }
    }

    fun display(res: Int, imageView: ImageView?) {
        if (null == imageView) {
            return
        }
        imageView.setTag(res)
        mExecutor.submit {
            doDecode(res, imageView)
        }
    }

    fun decodeType(path: String, width: Int, height: Int): Bitmap? {
        val index = path.lastIndexOf(".")
        val extension = if (-1 == index) {
            ""
        } else {
            path.substring(index).toLowerCase()
        }
        when (extension) {
            ".jpg", ".png", ".jpeg" -> return decodePic(path, width, height)

            ".mp4", ".mkv" -> return decodeVideo(path, width, height)

            ".apk" -> return decodeApk(path, width, height)

            else -> return decodeOther(path, width, height)
        }
    }

    fun decodeApk(path: String, width: Int, height: Int): Bitmap? {
        val manager = mContext.packageManager
        val info = manager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES)
        if (null != info) {
            val applicationInfo = info.applicationInfo
            applicationInfo.sourceDir = path
            applicationInfo.publicSourceDir = path
            val drawable = applicationInfo?.loadIcon(manager)
            return when(drawable){

                is BitmapDrawable->drawable.bitmap

                is AdaptiveIconDrawable->{
                    val foreground=drawable.foreground
                    val background=drawable.background
                    val layerDrawable=LayerDrawable(arrayOf(background,foreground))
                    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    layerDrawable.setBounds(0,0,bitmap.width,bitmap.height)
                    layerDrawable.draw(canvas)
                    bitmap
                }

                else->decodeOther(path, width, height)
            }
        }
        return decodeOther(path, width, height)
    }

    fun decodePic(path: String, requireWidth: Int, requireHeight: Int): Bitmap? {
        val origin = decodeWH(path)
        val sample = sample(origin.first, origin.second, requireWidth, requireHeight)
        return decode(path, sample)
    }

    fun decodeVideo(path: String, width: Int, height: Int): Bitmap? {
        val resolver = mContext.contentResolver
        val cursor = resolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Video.Thumbnails.DATA),
                "${MediaStore.Video.Thumbnails.KIND}=${MediaStore.Video.Thumbnails.MINI_KIND} AND ${MediaStore.Video.Thumbnails.VIDEO_ID} = ?",
                arrayOf(toUri(path).lastPathSegment),
                null)
        if (null == cursor || !cursor.moveToFirst()) {
            return decodeOther(path, width, height)
        }
        with(cursor, {
            val realPath = cursor.getString(0)
            if (TextUtils.isEmpty(path)) {
                return decodeOther(path, width, height)
            }
            return decodePic(realPath, width, height)
        })
    }

    fun decodeOther(path: String, width: Int, height: Int): Bitmap? {
        return TextDrawable.build(path.substring(path.indexOfLast { it == '/' } + 1), width, height)
    }

    fun doDecode(path: String, imageView: ImageView) {
        DimensionObserver(imageView, { width, height ->
            val bitmap = decodeType(path, width, height)
            if (null == bitmap) {
                Log.e(TAG, "$path 图片解析失败")
            } else if (path != imageView.tag.toString()) {
                mCache.put(path, bitmap)
                Log.e(TAG, "$path 跳过设置，因为控件已失效")
            } else {
                mCache.put(path, bitmap)
                imageView.setImageBitmap(bitmap)
            }
        })
    }

    fun doDecode(res: Int, imageView: ImageView) {
        DimensionObserver(imageView, { width, height ->
            val origin = decodeWH(res)
            val sample = sample(origin.first, origin.second, width, height)
            val bitmap=decode(res, sample)
            bitmap?.let {
                imageView.setImageBitmap(it)
            }
        })
    }

    private fun toUri(path: String): Uri {
        return Uri.fromFile(File(path))
    }

    private fun decodeWH(path: String): Pair<Int, Int> {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        BitmapFactory.decodeFile(path, options)
        return options.outWidth to options.outHeight
    }

    private fun decodeWH(res: Int): Pair<Int, Int> {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(mContext.resources,res, options)
        return options.outWidth to options.outHeight
    }

    private fun sample(oriWidth: Int, oriHeight: Int, width: Int, height: Int): Int {
        var simple = 1
        var tempW = oriWidth
        var tempH = oriHeight
        while (tempW > width || tempH > height) {
            simple *= 2
            tempW /= 2
            tempH /= 2
        }
        return simple
    }

    private fun decode(path: String, sample: Int): Bitmap? {
        val options = BitmapFactory.Options()
        options.inSampleSize = sample
        return BitmapFactory.decodeFile(path, options)
    }

    private fun decode(res: Int, sample: Int): Bitmap? {
        val options = BitmapFactory.Options()
        options.inSampleSize = sample
        return BitmapFactory.decodeResource(mContext.resources,res, options)
    }
}