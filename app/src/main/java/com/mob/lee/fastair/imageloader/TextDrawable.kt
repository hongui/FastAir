package com.mob.lee.fastair.imageloader

import android.graphics.*
import android.text.TextPaint
import androidx.annotation.ColorInt
import java.util.*
import java.util.regex.Pattern

/**
 * Created by Andy on 2017/11/6.
 */
object TextDrawable {
    val mPaint:TextPaint
    val mBound: Rect

    init {
        mPaint=TextPaint()
        mPaint.isAntiAlias = true
        mPaint.color = Color.WHITE
        mBound = Rect(0, 0, 0, 0)
    }

    @ColorInt
    val RED = Color.parseColor("#F44336")

    @ColorInt
    val PINK = Color.parseColor("#E91E63")

    @ColorInt
    val PURPLE = Color.parseColor("#9C27B0")

    @ColorInt
    val DEEP_PURPLE = Color.parseColor("#673AB7")

    @ColorInt
    val INDIGO = Color.parseColor("#3F51B5")

    @ColorInt
    val BLUE = Color.parseColor("#2196F3")

    @ColorInt
    val LIGHT_BLUE = Color.parseColor("#03A9F4")

    @ColorInt
    val CYAN = Color.parseColor("#00BCD4")

    @ColorInt
    val TEAL = Color.parseColor("#009688")

    @ColorInt
    val GREEN = Color.parseColor("#4CAF50")

    @ColorInt
    val LIGHT_GREEN = Color.parseColor("#4CAF50")

    @ColorInt
    val LIME = Color.parseColor("#CDDC39")

    @ColorInt
    val YELLOW = Color.parseColor("#FFEB3B")

    @ColorInt
    val AMBER = Color.parseColor("#FFC107")

    @ColorInt
    val ORANGE = Color.parseColor("#FF9800")

    @ColorInt
    val DEEP_ORANGE = Color.parseColor("#FF5722")

    @ColorInt
    val BROWN = Color.parseColor("#795548")

    @ColorInt
    val GREY = Color.parseColor("#9E9E9E")

    @ColorInt
    val BLUE_GREY = Color.parseColor("#607D8B")

    val MATERIAL_COLORS = arrayOf(RED, PINK, PURPLE, DEEP_PURPLE,
            INDIGO, BLUE, LIGHT_BLUE, CYAN, TEAL,
            GREEN, LIGHT_GREEN, LIME, YELLOW, AMBER,
            ORANGE, DEEP_ORANGE, BROWN, GREY, BLUE_GREY)

    fun pickColor(): Int {
        val index = Random(System.currentTimeMillis()).nextInt(MATERIAL_COLORS.size)
        return MATERIAL_COLORS[index]
    }

    fun build(text: String, width:Int,height:Int,color: Int = pickColor()):Bitmap{
        val singleText = configString(text)
        initPaint(singleText,width,height)
        val x=width/2F- mBound.centerX()
        val y=height/2F+ mBound.height()/2

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
        val canvas=Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        val currentColor = mPaint.color
        mPaint.color=color
        canvas.drawRoundRect(RectF(0F, 0F, width.toFloat(), height.toFloat()), width / 8F, height / 8F, mPaint)
        mPaint.color=currentColor
        canvas.drawText(singleText,0,singleText.length,x,y, mPaint)
        return bitmap
    }

    private fun configString(text: String):String{
        val pattern = Pattern.compile("\\w|[\\u4e00-\\u9fa5]")
        val result = pattern.toRegex().find(text)
        return result?.value?.toUpperCase()?:"A"
    }

    private fun initPaint(text: String,width: Int,height: Int){
        val size=Math.min(width,height)
        mPaint.textSize=0.618F*size

        mPaint.getTextBounds(text,0,1, mBound)
    }
}