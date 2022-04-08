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
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.ImageView
import java.io.File

class Decoder {
    companion object{
        const val TAG="Decoder"
        fun decode(context: Context?,path: String, imageView: ImageView,action:(Bitmap?)->Unit) {
            context?:return action(null)
            DimensionObserver(imageView, { width, height ->
                val bitmap = decodeType(context,path, width, height)
                action(bitmap)
            })
        }

        fun decodeType(context: Context,path: String, width: Int, height: Int): Bitmap? {
            val index = path.lastIndexOf(".")
            val extension = if (-1 == index) {
                ""
            } else {
                path.substring(index).lowercase()
            }
            when (extension) {
                ".jpg", ".png", ".jpeg" -> return decodePic(path, width, height)

                ".mp4", ".mkv" -> return decodeVideo(context,path, width, height)

                ".apk" -> return decodeApk(context,path, width, height)

                else -> return decodeOther(path, width, height)
            }
        }

        fun decodeApk(context: Context,path: String, width: Int, height: Int): Bitmap? {
            val manager = context.packageManager
            val info = manager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES)
            if (null != info) {
                val applicationInfo = info.applicationInfo
                applicationInfo.sourceDir = path
                applicationInfo.publicSourceDir = path
                val drawable = applicationInfo?.loadIcon(manager)
                return when(drawable){

                    is BitmapDrawable ->drawable.bitmap

                    is AdaptiveIconDrawable ->{
                        val foreground=drawable.foreground
                        val background=drawable.background
                        val layerDrawable= LayerDrawable(arrayOf(background, foreground))
                        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(bitmap)
                        layerDrawable.setBounds(0,0,bitmap.width,bitmap.height)
                        layerDrawable.draw(canvas)
                        bitmap
                    }

                    else-> decodeOther(path, width, height)
                }
            }
            return decodeOther(path, width, height)
        }

        fun decodePic(path: String, requireWidth: Int, requireHeight: Int): Bitmap? {
            val origin = decodeWH(path)
            val sample = sample(origin.first, origin.second, requireWidth, requireHeight)
            return decode(path, sample)
        }

        fun decodeVideo(context: Context,path: String, width: Int, height: Int): Bitmap? {
            val resolver = context.contentResolver
            val cursor = resolver.query(
                MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
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

        private fun toUri(path: String): Uri {
            return Uri.fromFile(File(path))
        }

        private fun decodeWH(path: String): Pair<Int, Int> {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true

            BitmapFactory.decodeFile(path, options)
            return options.outWidth to options.outHeight
        }

        private fun decodeWH(context: Context,res: Int): Pair<Int, Int> {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeResource(context.resources, res, options)
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
    }
}