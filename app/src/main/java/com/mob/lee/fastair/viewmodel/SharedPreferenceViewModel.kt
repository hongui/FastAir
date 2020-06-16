package com.mob.lee.fastair.viewmodel

import android.content.Context
import android.content.SharedPreferences

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/15 18:42
 * @Description:    无
 */
open class SharedPreferenceViewModel : AppViewModel() {

    fun readPreference(context: Context?, name: String, action: SharedPreferences.() -> Unit) {
        context?.getSharedPreferences(name, Context.MODE_PRIVATE)?.let { action(it) }
    }

    fun writePreference(context: Context?, name: String, action: SharedPreferences.Editor.() -> Unit) {
        context?.getSharedPreferences(name, Context.MODE_PRIVATE)?.edit()?.let {
            action(it)
            it.commit()
        }
    }
}