package com.mob.lee.fastair

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.mob.lee.fastair.viewmodel.AppViewModel
import com.mob.lee.fastair.viewmodel.PermissionViewModel
import kotlin.reflect.KClass

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
            requestPermissions(it, PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PERMISSION == requestCode) {
            ViewModelProviders.of(requireActivity()).get(PermissionViewModel::class.java).permissionLiveData.value = grantResults
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