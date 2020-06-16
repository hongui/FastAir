package com.mob.lee.fastair.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/15 16:33
 * @Description:    无
 */
class PermissionViewModel :ViewModel(){
    val permissionLiveData = MutableLiveData<IntArray>()
}