package com.mob.lee.fastair.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * Created by Andy on 2017/11/16.
 */
class TranslationBehavior : CoordinatorLayout.Behavior<View> {

    var mAnimating = false
    var mMargin:CoordinatorLayout.LayoutParams?=null
    var mTotalOffset=0

    constructor():super()

    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return 0!=(ViewCompat.SCROLL_AXIS_VERTICAL and axes)
    }

    override fun onNestedPreScroll(coordinatorLayout : CoordinatorLayout, child : View, target : View, dx : Int, dy : Int, consumed : IntArray, type : Int) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        if(mAnimating){
            return
        }
        val layoutparams=child.layoutParams as? CoordinatorLayout.LayoutParams
        val total=child.height+(layoutparams?.bottomMargin?:0).toFloat()
        if(dy>0&&child.translationY!=total){
            //往下滑，并且还没到底
            anim(child,0F,total)
        }else if(dy<0 &&0<child.translationY){
            //往上滑，并且还没恢复到原始位置
            anim(child,total,0F)
        }
    }

    fun anim(view: View,start:Float,end:Float) {
        mAnimating = true
        val animator = ObjectAnimator.ofFloat(view, "translationY",start,end)
        animator.duration=250
        animator.setInterpolator(FastOutSlowInInterpolator())
        animator.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator?) {
                mAnimating = false
            }
        })
        animator.start()
    }
}