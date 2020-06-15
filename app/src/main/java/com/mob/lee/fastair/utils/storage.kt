package com.mob.lee.fastair.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.preference.PreferenceManager
import com.mob.lee.fastair.R
import java.io.File
import java.util.ArrayList

fun Context.getPaths(file: File?): List<File> {
    var path = file
    val list = ArrayList<File>()
    if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        if (null == path) {
            path = Environment.getExternalStorageDirectory()
        }
        val files = path?.listFiles { dir, name ->
            dir.isDirectory && !name.startsWith(".")
        }
        files?.sortBy { it.name.toLowerCase() }
        files?.let {
            for (file in files) {
                list.add(file)
            }
        }
    }
    return list
}

fun Context.updateStorage(path: String?) {
    path ?: return
    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    intent.data = Uri.fromFile(File(path))
    this.sendBroadcast(intent)
}

fun Context.openFile(file: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val uri = FileProvider.getUriForFile(
                this,
                "${this.getPackageName()}.FileProvider",
                File(file))
        this.grantUriPermission(this.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        uri
    } else {
        Uri.fromFile(File(file))
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        this.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        errorToast(R.string.no_application_find)
    }
}

fun Context.createFile(name: String): File {
    val path = readDownloadPath()
    var file = File(path, name)
    val key = resources.getString(R.string.key_default_rewrite)
    val overrite = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(key, true)
    if (file.exists() && !overrite) {
        var i = 0
        val tempName = file.nameWithoutExtension
        val suf = file.extension
        while (file.exists()) {
            file = File(path, "$tempName($i).$suf")
            i++
        }
    }
    return file
}

fun Context.readDownloadPath(): String {
    val key = resources.getString(R.string.key_default_download)
    return PreferenceManager.getDefaultSharedPreferences(this).getString(key, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)
            ?: ""
}

fun Context.writeDownloadPath(path: String) {
    val infos = getSharedPreferences("infos", Context.MODE_PRIVATE)
    infos.edit().putString("downloadPath", path).apply()
}