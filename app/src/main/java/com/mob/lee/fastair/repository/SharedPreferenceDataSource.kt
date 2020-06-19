package com.mob.lee.fastair.repository

import android.content.Context
import android.content.SharedPreferences

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/19 14:01
 * @Description:    无
 */
class SharedPreferenceDataSource :DataSource{
    fun readPreference(context: Context?, name: String, action: SharedPreferences.() -> Unit) {
        context?.getSharedPreferences(name, Context.MODE_PRIVATE)?.let { action(it) }
    }

    fun writePreference(context: Context?, name: String, action: SharedPreferences.Editor.() -> Unit) {
        writePreference(context?.getSharedPreferences(name, Context.MODE_PRIVATE),action)
    }

    fun writePreference(shared: SharedPreferences?, action: SharedPreferences.Editor.() -> Unit) {
        shared?.edit()?.let {
            action(it)
            it.commit()
        }
    }
}