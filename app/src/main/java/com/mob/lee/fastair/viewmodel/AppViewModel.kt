package com.mob.lee.fastair.viewmodel

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.mob.lee.fastair.PermissionFragment
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.AppListAdapter
import com.mob.lee.fastair.adapter.SingleAdapter
import com.mob.lee.fastair.model.DataLoad
import com.mob.lee.fastair.model.DataWrap
import kotlinx.android.synthetic.main.fragment_content_pick.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class AppViewModel : ViewModel() {
    val stateLiveData = DataLoad<Any?>()

    fun <D> async(liveData: MutableLiveData<D>? = null, action: suspend DataLoad<D>.() -> Unit): LiveData<D> {
        val targetLiveData = liveData.apply { this?.value = null } ?: MutableLiveData<D>()

        val dataLoad = DataLoad<D>()

        val observer = Observer<Pair<Int, D?>> {
            stateLiveData.value = it
            if (it.first == DataLoad.NEXT) {
                targetLiveData.value = it?.second
            }
        }

        dataLoad.observeForever(observer)

        viewModelScope.launch(Dispatchers.Main) {
            try {
                action(dataLoad)

                when (dataLoad.code) {
                    DataLoad.LOADING -> dataLoad.empty()
                    DataLoad.NEXT -> dataLoad.complete()
                }
                dataLoad.removeObserver(observer)
            } catch (e: Exception) {
                dataLoad.error(e.message)
            } finally {
                stateLiveData.value = null
            }
        }
        return targetLiveData
    }

    fun <D> asyncWithWrap(liveData: MutableLiveData<DataWrap<D>>? = null, action: suspend () -> DataWrap<D>): LiveData<DataWrap<D>> = async(liveData) {
        val data = action()
        next(data)
    }

    fun withPermission(fragment: Fragment, vararg permissions: String, action: (Int: Int, hasPermission: Boolean) -> Unit) {
        val target = ArrayList<String>()
        permissions.forEachIndexed { index, s ->
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(fragment.requireContext(), s)) {
                target.add(s)
            } else {
                action(index, true)
            }
        }
        if (target.isNotEmpty()) {
            val viewmodel:PermissionViewModel by fragment.requireActivity().viewModels()
            val fragmentManager = fragment.activity?.supportFragmentManager
            val f = PermissionFragment.request(target)

            viewmodel.permissionLiveData.observe(f, Observer {
                it ?: return@Observer
                viewmodel.permissionLiveData.value = null
                fragmentManager?.beginTransaction()?.remove(f)?.commit()
                it.forEachIndexed { index, state ->
                    action(index, PackageManager.PERMISSION_GRANTED == state)
                }
            })

            fragmentManager?.beginTransaction()?.add(android.R.id.content, f, "permission")
                    ?.show(f)
                    ?.commit()
        }
    }

    fun <D> watchState(fragment: Fragment, rv: RecyclerView, contentAdapter: AppListAdapter<D>) {
        stateLiveData.observe(fragment, Observer {
            it ?: return@Observer
            when (it.first) {
                DataLoad.LOADING -> {
                    rv.adapter = SingleAdapter(R.layout.loading)
                }

                DataLoad.EMPTY -> {
                    rv.adapter = SingleAdapter(R.layout.empty)
                }

                DataLoad.STARTED -> {
                    contentAdapter.clear()
                    rv.adapter = contentAdapter
                }
            }
        })
    }

    fun openSetting(fragment:Fragment){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", fragment.context?.packageName, null)
        intent.data = uri
        fragment.startActivityForResult(intent, 123)
    }
}