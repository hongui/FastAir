package com.mob.lee.fastair

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mob.lee.fastair.viewmodel.PermissionViewModel

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/15 13:45
 * @Description:    无
 */
class PermissionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View(layoutInflater.context)
    }


    override fun onStart() {
        super.onStart()
        arguments?.getStringArray("permissions")?.let {
            val permission = ActivityResultContracts.RequestMultiplePermissions()
            val res = registerForActivityResult(permission, object : ActivityResultCallback<Map<String,Boolean>> {

                override fun onActivityResult(result: Map<String, Boolean>?) {

                }
            })
            res.launch(it)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PERMISSION == requestCode) {
            val model:PermissionViewModel by requireActivity().viewModels()
            model.permissionLiveData.value = grantResults
        }
    }

    companion object {
        const val PERMISSION = 13

        fun request(permissions: List<String>): Fragment {
            val fragment = PermissionFragment()
            fragment.arguments = Bundle().apply { putStringArray("permissions", permissions.toTypedArray()) }
            return fragment
        }
    }
}