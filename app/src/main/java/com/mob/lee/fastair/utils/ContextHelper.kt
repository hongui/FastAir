package com.mob.lee.fastair.utils

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.mob.lee.fastair.ContainerActivity
import com.mob.lee.fastair.R
import com.mob.lee.fastair.db.AppDatabase
import com.mob.lee.fastair.db.RecordDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Andy on 2017/7/7.
 */

fun Context.toast(message: String?, bg: Int) {
    if (null == message) {
        return
    }
    if (this is ContainerActivity) {
        Snackbar.make(findViewById(R.id.root_layout), message, Snackbar.LENGTH_SHORT).apply {
            setTextColor(Color.WHITE)
            view.setBackgroundResource(bg)
            show()
        }
    }
}

fun Context.errorToast(message: Int) {
    val msg = getString(message)
    msg ?: return
    errorToast(msg)
}

fun Context.errorToast(message: String?) {
    toast(message, R.drawable.bg_toast_failed)
}

fun Context.successToast(message: Int) {
    val msg = getString(message)
    msg ?: return
    successToast(msg)
}

fun Context.successToast(message: String) {
    toast(message, R.drawable.bg_toast_success)
}

fun Context.dialog(wrap: AlertDialog.Builder.() -> Unit) {
    val builder = AlertDialog.Builder(this)
        .setTitle(R.string.wram_tips)
    wrap(builder)
    builder.show()
}

fun Context.database(scope: CoroutineScope, action: suspend (RecordDao) -> Unit) {
    val database = "fastair"
    var db: AppDatabase? = null
    try {
        db = Room.databaseBuilder(this, AppDatabase::class.java, database).build()
        val dao = db.recordDao()
        scope.launch(Dispatchers.IO) {
            action(dao)
            db.close()
        }
    } catch (e: Exception) {
        Log.d(database, e.toString())
        db?.close()
    }
}