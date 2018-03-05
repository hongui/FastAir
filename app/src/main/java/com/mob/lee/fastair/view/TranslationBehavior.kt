package com.mob.lee.fastair.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.ViewCompat
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.util.AttributeSet
import android.view.View

/**
 * Created by Andy on 2017/11/16.
 */
class TranslationBehavior : CoordinatorLayout.Behavior<View> {

    var mAnimating = false
    var mMargin:CoordinatorLayout.LayoutParams?=null

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    /*开始滑动*/

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return 0!=(ViewCompat.SCROLL_AXIS_VERTICAL and axes)
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int) {
        if(null==mMargin){
            mMargin=child.layoutParams as CoordinatorLayout.LayoutParams
        }
        if (0F == child.translationY && !mAnimating && (0 < dyUnconsumed && 0 == dyConsumed/*划到底部并且继续划*/ || 0 == dyUnconsumed && 0 < dyConsumed/*往下滑未到底*/)) {
            anim(child, 0F, mMargin!!.bottomMargin+child.height.toFloat())
        } else if ((mMargin!!.bottomMargin+child.height).toFloat() == child.translationY && !mAnimating && (0 > dyUnconsumed && 0 == dyConsumed || 0 == dyUnconsumed && 0 > dyConsumed)) {
            anim(child, mMargin!!.bottomMargin+child.height.toFloat(), 0F)
        }
    }

    fun anim(view: View,start:Float,end:Float) {
        val animator = ObjectAnimator.ofFloat(view, "translationY",start,end)
        animator.setInterpolator(FastOutSlowInInterpolator())
        animator.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animation: Animator?) {
                mAnimating = true
            }

            override fun onAnimationEnd(animation: Animator?) {
                mAnimating = false
            }
        })
        animator.start()
    }
}