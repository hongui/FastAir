package com.mob.lee.fastair.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.mob.lee.fastair.R

/**
 * Created by Andy on 2017/6/1.
 */

abstract class AppFragment : Fragment() {

    var mParent : AppActivity? = null
    val mScope : AndroidScope by lazy {
        AndroidScope()
    }

    override fun onAttach(context : Context?) {
        super.onAttach(context)
        context?.let {
            mParent = it as AppActivity
        }
    }

    override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        return inflater?.inflate(layout(), container, false)
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mScope.create()
        setting()
    }

    override fun onDestroy() {
        super.onDestroy()
        mScope.destory()
    }

    abstract fun layout() : Int

    open fun setting() {}

    fun toolbar(titleId : Int, canReturn : Boolean = true) {
        toolbar(getString(titleId), canReturn)
    }

    fun toolbar(title : CharSequence, canReturn : Boolean = true) {
        val toolbar = view?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.setTitle(title)
        val appActivity = activity as AppActivity
        appActivity.setSupportActionBar(toolbar)
        if (canReturn) {
            appActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar?.setNavigationOnClickListener { appActivity?.onBackPressed() }
        }
    }
}