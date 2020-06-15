package com.mob.lee.fastair.viewmodel

import android.content.Context
import android.os.Environment
import androidx.lifecycle.MutableLiveData
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
    private val sharedPreferenceName="info"
    protected val sharedPreferenceKey="DownloadPath"
    private var currentPath: File? = null
    var mCurrentPositionLiveData=MutableLiveData<Int>()
    var pathLiveData=MutableLiveData<File>()

    fun updatePath(context: Context?,path:File?=null) {
        readPreference(context,sharedPreferenceName){
            currentPath = path?:File(getString(sharedPreferenceKey,""))
            getPaths(currentPath)
        }
    }

    fun getPaths(path:File?=null)=async<File> {
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            var target=path
            if (null == path) {
                target = Environment.getExternalStorageDirectory()
            }
            val files = target?.listFiles { dir, name ->
                dir.isDirectory && !name.startsWith(".")
            }
            files?.sortBy { it.name.toLowerCase() }
            files?.forEach {
                next(it)
            }
        }
    }

    fun submit(fragment: AppFragment){
        writePreference(fragment.requireContext(),sharedPreferenceName){
            putString(sharedPreferenceKey,currentPath?.absolutePath)
            fragment.mParent?.successToast(fragment.getString(R.string.path_setting_success,currentPath?.getAbsolutePath()))
            fragment.mParent?.onBackPressed()
        }
    }
}