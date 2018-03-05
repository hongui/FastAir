package com.mob.lee.fastair.base

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mob.lee.fastair.R

/**
 * Created by Andy on 2017/6/1.
 */

abstract class AppFragment :Fragment(){

    var mParent:AppActivity?=null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context?.let {
            mParent=it as AppActivity
        }
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(layout(),container,false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    abstract fun layout():Int

    open fun setting(){}

    fun toolbar(titleId: Int, canReturn: Boolean = true) {
        toolbar(getString(titleId),canReturn)
    }

    fun toolbar(title: CharSequence,canReturn: Boolean=true){
        val toolbar = view?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.setTitle(title)
        val appActivity=activity as AppActivity
        appActivity.setSupportActionBar(toolbar)
        if (canReturn) {
            appActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar?.setNavigationOnClickListener{ appActivity?.onBackPressed() }
        }
    }

    fun showDialog(content: CharSequence,positiveListener: (dialog:DialogInterface,which:Int)->Unit,positive: CharSequence=getString(R.string.ok),title: CharSequence=getString(R.string.wramTips),negative: CharSequence=getString(R.string.justkid),negativeListener: ((dialog:DialogInterface,which:Int)->Unit)?=null){
        AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(positive,positiveListener)
                .setNegativeButton(negative,negativeListener)
                .show()
    }

    fun toast(content:Int,duration:Int=Toast.LENGTH_SHORT){
        Toast.makeText(context,content,duration).show()
    }

    fun toast(content:CharSequence,duration:Int=Toast.LENGTH_SHORT){
        Toast.makeText(context,content,duration).show()
    }
}