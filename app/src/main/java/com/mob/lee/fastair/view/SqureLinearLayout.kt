package com.mob.lee.fastair.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

/**
 * Created by Andy on 2017/9/1.
 */
class SqureLinearLayout:LinearLayout{
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}