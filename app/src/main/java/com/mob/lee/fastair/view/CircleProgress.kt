package com.mob.lee.fastair.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

/**
 * Created by Andy on 2017/8/31.
 */
class CircleProgress : View {
    private lateinit var mPaint: Paint
    private lateinit var mTextPaint: TextPaint
    private lateinit var mRectF: RectF
    private lateinit var mBounds: Rect
    private lateinit var mPath: Path
    private lateinit var mTempPath: Path
    private lateinit var mPathMeasure: PathMeasure

    private var progress = 0F
    private var max = 100F
    private var state = PROGRESS
    private var reverse = false

    companion object {
        val PROGRESS = 0
        val SUCCESS = 1
        val FAILED = 2
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        val minSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32F, context.resources.displayMetrics)
        if (MeasureSpec.EXACTLY != widthMode) {
            width = minSize.toInt()
        }
        if (MeasureSpec.EXACTLY != heightMode) {
            height = minSize.toInt()
        }
        width = minOf(width, height)
        val spac = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        setMeasuredDimension(spac, spac)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val stokeWidth = w / 20F
        val textSize = 0.618585F * w / 2
        mPaint.strokeWidth = stokeWidth
        mTextPaint.textSize = textSize
        val half = stokeWidth / 2
        mRectF.left = half
        mRectF.top = half
        mRectF.right = w - half
        mRectF.bottom = h - half
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (null == canvas) {
            return
        }
        when (state) {
            PROGRESS -> progress(canvas)

            SUCCESS, FAILED -> {
                comcute(canvas)
                val half = width / 2
                canvas.drawCircle(half.toFloat(), half.toFloat(), half - mRectF.left, mPaint)
                canvas.drawPath(mTempPath, mPaint)
                if ((SUCCESS == state && progress <= max) || (FAILED==state&&!reverse)||(FAILED==state&&reverse&&progress<=max)) {
                    postInvalidateDelayed(15)
                }
            }
        }
    }

    fun updateState(state: Int) {
        this.state = state
        progress = 0F
        reverse = false
        when (state) {
            SUCCESS -> {
                mPath = Path()
                mTempPath = Path()
                mPathMeasure = PathMeasure()
                mPath.moveTo(width / 3.14F, width / 2F)
                mPath.lineTo(width / 2F, width / 5F * 3)
                mPath.lineTo(width / 3F * 2, width / 5F * 2)
                mPathMeasure.setPath(mPath, false)
                max = mPathMeasure.length
            }

            FAILED -> {
                mPath = Path()
                mTempPath = Path()
                mPathMeasure = PathMeasure()
                mPath.moveTo(width / 3F * 2, width / 3F)
                mPath.lineTo(width / 3F, width / 3F * 2)
                mPathMeasure.setPath(mPath, false)
                max = mPathMeasure.length
            }
        }
        invalidate()
    }

    fun progress(progress: Float) {
        this.progress = progress
        if (max <= progress) {
            updateState(SUCCESS)
        }
        this.state= PROGRESS
        invalidate()
    }

    fun max(max: Float) {
        this.max = max
        invalidate()
    }

    private fun init() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        val attrs = intArrayOf(android.R.attr.colorPrimary, android.R.attr.colorAccent)
        val typedArray = context.obtainStyledAttributes(attrs)
        val primary = typedArray.getColor(0, Color.BLACK)
        val accent = typedArray.getColor(1, Color.BLACK)

        mPaint = Paint()
        mPaint.flags = Paint.ANTI_ALIAS_FLAG
        mPaint.color = primary
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.ROUND

        mTextPaint = TextPaint()
        mTextPaint.flags = TextPaint.ANTI_ALIAS_FLAG
        mTextPaint.color = accent

        mRectF = RectF(0F, 0F, 0F, 0F)
        mBounds = Rect(0, 0, 0, 0)
    }

    private fun progress(canvas: Canvas) {
        val content = progress.toInt().toString()
        mTextPaint.getTextBounds(content, 0, content.length, mBounds)
        val half = width / 2
        val x = half - mBounds.centerX().toFloat()
        val y = half - mBounds.centerY().toFloat()
        canvas.drawText(content, x, y, mTextPaint)

        val color = mPaint.color
        mPaint.color = Color.parseColor("#FFE0E0E0")
        canvas.drawCircle(half.toFloat(), half.toFloat(), half - mRectF.left, mPaint)
        mPaint.color = color
        canvas.drawArc(mRectF, -90F, progress * 360 / max, false, mPaint)
    }

    private fun comcute(canvas: Canvas) {
        progress = progress + 1
        when (state) {
            SUCCESS -> {
                mPathMeasure.getSegment(0F, progress, mTempPath, true)
            }

            FAILED -> {
                if (max <= progress && !reverse) {
                    reverse = true
                    mPath.reset()
                    mPath.moveTo(width / 3F, width / 3F);
                    mPath.lineTo(width / 3F * 2, width / 3F * 2)
                    mPathMeasure.setPath(mPath, false)
                    max=mPathMeasure.length
                    progress = 0F
                } else if(reverse){
                    mPathMeasure.getSegment(0F, progress, mTempPath, true)
                    canvas.drawLine(width / 3F, width / 3F, width / 3F * 2, width / 3F * 2, mPaint)
                }else{
                    mPathMeasure.getSegment(0F, progress, mTempPath, true)
                }
            }
        }
    }
}