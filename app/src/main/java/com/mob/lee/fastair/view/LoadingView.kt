package com.mob.lee.fastair.view

import android.content.Context
import android.util.AttributeSet
import android.view.View

class LoadingView: View {

    constructor(context:Context):this(context,null)

    constructor(context : Context,attri:AttributeSet?):super(context,attri)

    override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }
}