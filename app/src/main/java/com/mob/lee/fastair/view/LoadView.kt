package com.mob.lee.fastair.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.mob.lee.fastair.R
import kotlin.math.*

class LoadView : View {

    var mCircleCount=13
    var mLastIndex=0
    var minSize=48F

    val mPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = ContextCompat.getColor(context, R.color.colorPrimary)
        }
    }
    val mCircles=Array(mCircleCount){
        Circle(0F,0F)
    }

    constructor(context: Context) : super(context, null)

    constructor(context: Context, attri: AttributeSet?) : super(context, attri)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width=MeasureSpec.getSize(widthMeasureSpec)
        var height=MeasureSpec.getSize(heightMeasureSpec)
        val size=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,minSize,context.resources.displayMetrics).toInt()
        if(MeasureSpec.UNSPECIFIED!=MeasureSpec.getMode(widthMeasureSpec)){
            width=size
        }
        if(MeasureSpec.UNSPECIFIED!=MeasureSpec.getMode(heightMeasureSpec)){
            height=size
        }
        setMeasuredDimension(width,height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val centerX = width / 2F
        val centerY = height / 2F
        val contentWidth = min(centerX - paddingLeft, centerY - paddingTop)
        val smallRadiu = contentWidth / 10F
        val bigRadiu = contentWidth - smallRadiu
        for(c in mCircles){
            c.radius=smallRadiu
            c.bigRadius=bigRadiu
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val centerX = width / 2F
        val centerY = height / 2F
        if(mLastIndex<mCircleCount-1){
            val circle=mCircles[mLastIndex]
            val angle= sin(circle.radius/2/circle.bigRadius)*180/ PI

            if(circle.angle+90> 5*angle){
                mLastIndex+=1
            }
        }
        for(i in 0 .. mLastIndex){
            val c=mCircles[i]
            c.update()
            val x = centerX + c.x
            val y = centerY + c.y
            canvas?.drawCircle(x, y, c.radius, mPaint)
        }
        invalidate()
    }

    data class Circle(var radius:Float,var bigRadius: Float) {
        var x: Float = 0F
        var y: Float = 0F
        var angle: Float = -90F
        var acceleration=3.14F

        fun update() {
            if (angle >= 270) {
                angle = -90F
            }else {
                angle += acceleration
            }
            val rate=(angle+90)/360
            val output = (cos((rate + 1) * Math.PI.toFloat()) / 2.0F) + 0.5F
            val radian = (-90+360*output) * (PI / 180).toFloat()
            x = bigRadius * cos(radian)
            y = bigRadius * sin(radian)
        }
    }
}