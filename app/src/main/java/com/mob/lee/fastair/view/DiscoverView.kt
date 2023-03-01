package com.mob.lee.fastair.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mob.lee.fastair.R
import java.util.*

/**
 * Created by Andy on 2017/8/11.
 */
class DiscoverView : ViewGroup {
    lateinit var paint: Paint
    var currentRadius = 0F
    var backColor: Int = 0
    var accentColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        init()
    }

    constructor(context: Context, attr: AttributeSet, defStyle: Int) : super(context, attr, defStyle) {
        init()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val count = childCount
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        measureChildren(widthMeasureSpec,heightMeasureSpec)
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (checkLayoutParams(child.layoutParams)) {
                val params = child.layoutParams as LayoutParams
                params.width=child.measuredWidth
                params.height=child.measuredHeight
                params.calculate(width-params.width, height-params.height)
            }
        }
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        for (i in 0 until count) {
            val view = getChildAt(i)
            val layoutParams = view.layoutParams as LayoutParams
            view.layout(layoutParams.x, layoutParams.y, layoutParams.x + layoutParams.width, layoutParams.y + layoutParams.height)
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        val width = width
        val height = height

        val x = (width / 2).toFloat()
        val y = height.toFloat()
        val den = (context.resources.displayMetrics.density * 56).toInt()

        if (currentRadius >= height) {
            currentRadius = den.toFloat()
        }

        val temp = currentRadius.toInt()
        paint.color=backColor
        paint.style=Paint.Style.STROKE
        paint.strokeWidth= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1F,resources.displayMetrics)
        for (i in den..temp step den) {
            paint.alpha=(i*1.0/temp*157.59).toInt()
            canvas?.drawCircle(x, y, currentRadius - i, paint)
        }
        paint.alpha=255
        paint.color=accentColor
        paint.style=Paint.Style.FILL
        canvas?.drawCircle(x, y, den.toFloat(), paint)

        currentRadius += 10
        postInvalidateDelayed(25)

        super.dispatchDraw(canvas)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        val layoutParams = DiscoverView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return layoutParams
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        val layoutParams = LayoutParams(context, attrs)
        return layoutParams
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams?): LayoutParams {
        val layoutParams = LayoutParams(p)
        return layoutParams
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
        return p is LayoutParams
    }

    fun init() {
        backColor = ContextCompat.getColor(context, R.color.colorPrimary)
        accentColor = ContextCompat.getColor(context, R.color.colorAccent)

        paint = Paint()
        paint.flags = Paint.ANTI_ALIAS_FLAG
        paint.style=Paint.Style.FILL
    }

    class LayoutParams : ViewGroup.MarginLayoutParams {
        val random = Random(System.currentTimeMillis())

        var x = 0
        var y = 0

        constructor(context: Context, attr: AttributeSet?) : super(context, attr)

        constructor(source: ViewGroup.LayoutParams?) : super(source)

        constructor(width: Int, height: Int) : super(width, height)

        fun calculate(width: Int, height: Int) {
            x = random.nextInt(width)
            y = random.nextInt(height)
        }
    }
}
