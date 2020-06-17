package com.mob.lee.fastair.viewmodel

import android.content.Context
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.utils.successToast
import java.io.File

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/15 18:29
 * @Description:    无
 */
class PathPickViewModel : SharedPreferenceViewModel() {
    private val sharedPreferenceName = "info"
    var currentPath: File? = null
    var currentPositionLiveData = MutableLiveData<Int>()
    var pathLiveData = MutableLiveData<List<File>>()

    fun updatePath(context: Context?, path: File? = null, pos: Int? = null) {
        context ?: return

        currentPath = path ?: if (null == currentPath) {
            Environment.getExternalStorageDirectory()
        } else {
            var distance = (currentPositionLiveData.value ?: 0) - (pos ?: 0)
            var file: File? = currentPath

            while (distance > 0) {
                file = file?.parentFile
                distance--
            }
            file
        }

        currentPositionLiveData.value = if (null != pos) {
            pos
        } else if (null != path && null != currentPositionLiveData.value) {
            currentPositionLiveData.value!! + 1
        } else if (null == path && null == currentPositionLiveData.value) {
            0
        } else {
            currentPositionLiveData.value
        }

        getPaths()
    }

    fun getPaths() = async(pathLiveData) {
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val files = currentPath?.listFiles { dir, name ->
                val file = File(dir, name)
                file.isDirectory && !file.isHidden
            }
            files?.sortBy { it.name.toLowerCase() }
            next(files?.toList())
        }
    }

    fun submit(fragment: AppFragment) {
        val key = fragment.getString(R.string.key_default_download)
        val defaultDownloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val manager = PreferenceManager.getDefaultSharedPreferences(fragment.requireContext())
        val value = manager.getString(key, defaultDownloadPath?.absolutePath)
        writePreference(manager) {
            putString(key, currentPath?.absolutePath ?: value)
        }
        fragment.mParent?.successToast(fragment.getString(R.string.path_setting_success, currentPath?.getAbsolutePath()))
        fragment.mParent?.onBackPressed()
    }
}