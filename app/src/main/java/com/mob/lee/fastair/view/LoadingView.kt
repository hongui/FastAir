package com.mob.lee.fastair.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import com.mob.lee.fastair.R
import java.lang.annotation.RetentionPolicy
import kotlin.math.min
import kotlin.properties.Delegates
import kotlin.random.Random
import kotlin.random.nextInt

class LoadingView : View {

    companion object {
        @JvmStatic
        val DEFAULT_SIZE = 96F

        val TAG = "LoadingView"

        const val RAIN_STATE_CREATE = 0

        const val RAIN_STATE_DROP = 1

        const val RAIN_STATE_REACH = 2

        const val RAIN_STATE_DIFFUSION = 3

        const val RAIN_STATE_END = 4
    }

    val mPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = ContextCompat.getColor(context, R.color.colorPrimary)
        }
    }

    val mRandom = Random(System.currentTimeMillis())

    val mRain = Array<Rain>(256, {
        genOrUpdate()
    })

    var mCuurentActive = 1

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attri: AttributeSet?) : super(context, attri)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        for (r in mRain) {
            genOrUpdate(r)
        }
        setLayerType(LAYER_TYPE_SOFTWARE, mPaint)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        drawRain(canvas)
    }

    fun drawRain(canvas: Canvas) {
        changeActive()
        for (r in mRain) {
            if (r.state == RAIN_STATE_CREATE) {
                continue
            }
            r.update(width, height)
            canvas.save()
            canvas.translate(r.x, r.y)
            mPaint.alpha = r.alpha
            canvas.drawPath(r.path, mPaint)
            canvas.restore()

        }
        postDelayed({ invalidate() }, 32)
    }

    fun genOrUpdate(raim: Rain? = null): Rain {
        val velocity = mRandom.nextFloat() * 50
        val quality = mRandom.nextFloat() * 20
        val x = mRandom.nextInt(if (0 == width) 2 else width).toFloat()
        val y = (mRandom.nextInt(-100, -20)).toFloat()
        return raim?.apply {
            this.velocity = velocity
            this.quility = quality
            this.x = x
            this.y = y
        } ?: Rain(velocity, quality, x, y).apply { state = RAIN_STATE_CREATE }
    }

    fun changeActive() {
        mCuurentActive += 1
        if (mCuurentActive > mRain.size) {
            mCuurentActive = mRain.size
        }
        var count = 0
        for (rain in mRain) {
            when{
                rain.state==RAIN_STATE_CREATE&&count < mCuurentActive -> {
                        rain.state = RAIN_STATE_DROP
                        count += 1
                }
                rain.state==RAIN_STATE_END&&count < mCuurentActive -> {
                    genOrUpdate(rain)
                    rain.state= RAIN_STATE_CREATE
                    rain.update(width, height)
                }

                rain.state>RAIN_STATE_CREATE -> rain.update(width, height)
            }
        }

    }

    data class Rain(
            var velocity: Float,
            var quility: Float,
            var x: Float,
            var y: Float,
            val width: Int = 16,
            val height: Int = 24
    ) {
        var alpha = 255
        var diffuseVel = 1F
        val path: Path
        lateinit var bound: RectF
        @RainState
        var state: Int = RAIN_STATE_CREATE

        init {
            path = Path()
            genRain()
        }

        fun update(totalWidth: Int,
                   totalHeight: Int) {
            when (state) {
                RAIN_STATE_CREATE -> {
                    state = RAIN_STATE_DROP
                    path.reset()
                    genRain()
                }

                RAIN_STATE_DROP -> {
                    alpha = 255
                    velocity += 0.013F * velocity
                    y += velocity
                    if (y + height >= 9 * totalHeight / 10) {
                        state = RAIN_STATE_REACH
                    }
                }

                RAIN_STATE_REACH -> {
                    state = RAIN_STATE_DIFFUSION
                    path.reset()
                    diffuseVel = quility / 10
                    bound = RectF(0F, 9 * height / 10F, width.toFloat(), height.toFloat())
                    path.addOval(bound, Path.Direction.CW)
                }

                RAIN_STATE_DIFFUSION -> {
                    diffuseVel += 0.098F * diffuseVel
                    alpha -= 20
                    bound.left -= diffuseVel
                    bound.top -= diffuseVel
                    bound.right += diffuseVel
                    bound.bottom += diffuseVel
                    path.reset()
                    path.addOval(bound, Path.Direction.CW)
                    if (0 >= alpha) {
                        alpha = 0
                        state = RAIN_STATE_END
                    }
                }

                RAIN_STATE_END -> {
                    path.reset()
                    genRain()
                }
            }
        }

        private fun genRain() {
            path.moveTo(width / 2F, height.toFloat())
            path.quadTo(width / 18F, 11 * height / 12F, 3 * width / 10F, 3 * height / 7F)
            path.quadTo(9 * width / 20F, 3 * height / 14F, width / 2F, 0F)
            path.quadTo(11 * width / 20F, 3 * height / 14F, 7 * width / 10F, 3 * height / 7F)
            path.quadTo(17 * width / 18F, 11 * height / 12F, width / 2F, height.toFloat())
        }
    }

    @IntDef(RAIN_STATE_CREATE, RAIN_STATE_DROP, RAIN_STATE_REACH, RAIN_STATE_DIFFUSION, RAIN_STATE_END)
    @Retention(AnnotationRetention.SOURCE)
    annotation class RainState {}
}


