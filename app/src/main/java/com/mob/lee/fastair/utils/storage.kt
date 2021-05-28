package com.mob.lee.fastair.utils

import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.loader.content.CursorLoader
import androidx.preference.PreferenceManager
import com.mob.lee.fastair.R
import java.io.File
import java.lang.Exception
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

fun Context.urlToPath(uri: Uri):String?{
        val sdkVersion = Build.VERSION.SDK_INT
        return if (sdkVersion < 11) return getRealPathFromUri_BelowApi11(this,uri)
        else if (sdkVersion < 19) return getRealPathFromUri_Api11To18(this,uri)
        else return getRealPathFromUri_AboveApi19(this,uri)
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

fun getRealPathFromUri_BelowApi11(context: Context?,uri: Uri): String? {
    var filePath: String? = null
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context?.contentResolver?.query(uri, projection, null, null, null)
    if (cursor != null && cursor.moveToFirst()) {
        filePath = cursor.getString(cursor.getColumnIndex(projection[0]))
        cursor.close()
    }
    return filePath
}

fun getRealPathFromUri_Api11To18(context: Context?,uri: Uri): String? {
    var filePath: String? = null
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    //这个有两个包不知道是哪个。。。。不过这个复杂版一般用不到
    val loader = CursorLoader(context!!, uri, projection, null, null, null)
    val cursor = loader.loadInBackground()

    if (cursor != null) {
        cursor.moveToFirst()
        filePath = cursor.getString(cursor.getColumnIndex(projection[0]))
        cursor.close()
    }
    return filePath
}

@TargetApi(Build.VERSION_CODES.KITKAT)
fun getRealPathFromUri_AboveApi19(context: Context?,uri: Uri): String? {
    if (DocumentsContract.isDocumentUri(context, uri)) {
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":")
            val type = split[0]
            if ("primary" == (type)) {
                return "${Environment.getExternalStorageDirectory()}/${split[1]}"
            }
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), id.toLong()
            )
            return getDataColumn(context,contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":")
            val type = split[0]

            var contentUri: Uri
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            } else {
                contentUri = MediaStore.Files.getContentUri("external")
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])
            return getDataColumn(context,contentUri, selection, selectionArgs)
        }

    } else if ("content" == uri.scheme) {
        return getDataColumn(context,uri, null, null)
    } else if ("file" == uri.scheme) {
        return uri.path
    }
    return null
}

fun getDataColumn(
    context: Context?,
    uri: Uri?,
    selection: String?,
    selectionArgs: Array<String>?
): String? {
    uri?:return null
    var cursor: Cursor? = null
    val column = MediaStore.MediaColumns.DATA
    val projection = arrayOf(column)
    try {
        cursor = context?.contentResolver?.query(
            uri, projection, selection, selectionArgs,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        if (cursor != null)
            cursor.close()
    }
    return null
}

private fun isExternalStorageDocument(uri: Uri)= "com.android.externalstorage.documents" == uri.authority

private fun isDownloadsDocument(uri: Uri)="com.android.providers.downloads.documents" == uri.authority

private fun isMediaDocument(uri: Uri)="com.android.providers.media.documents" == uri.authority