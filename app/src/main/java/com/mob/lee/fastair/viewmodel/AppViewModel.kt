package com.mob.lee.fastair.viewmodel

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.AppListAdapter
import com.mob.lee.fastair.adapter.SingleAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.model.DataLoad
import com.mob.lee.fastair.model.DataWrap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class AppViewModel : ViewModel() {
    val stateLiveData = DataLoad<Any?>()
    var mPermission: ActivityResultLauncher<String>? = null
    var mActivityResult: ActivityResultLauncher<Intent>? = null
    var mPermissionData = MutableLiveData<Boolean>()
    var mActivityResultData = MutableLiveData<ActivityResult?>()

    fun <D> async(
        liveData: MutableLiveData<D>? = null,
        action: suspend DataLoad<D>.() -> Unit
    ): LiveData<D> {
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

    fun <D> asyncWithWrap(
        liveData: MutableLiveData<DataWrap<D>>? = null,
        action: suspend () -> DataWrap<D>
    ): LiveData<DataWrap<D>> = async(liveData) {
        val data = action()
        next(data)
    }

    fun withPermission(
        fragment: AppFragment,
        permission: String,
        action: (hasPermission: Boolean) -> Unit
    ) {
        val hasPermission = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            permission
        )
        if (hasPermission) {
            action(true)
        } else if (null == mPermission) {
            throw IllegalArgumentException("Permission launcher is null,you should call registerPermission first")
        } else {
            if (fragment.shouldShowRequestPermissionRationale(permission)) {
                //TODO
            }
            val live = mPermissionData.distinctUntilChanged()
            live.observe(fragment, object : Observer<Boolean> {
                override fun onChanged(t: Boolean?) {
                    live.removeObserver(this)
                    action(t ?: false)
                }
            })
            mPermission?.launch(permission)

        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun withStoragePermissionROrHigher(
        fragment: AppFragment,
        permission: String,
        action: (hasPermission: Boolean) -> Unit
    ) {
        if (Environment.isExternalStorageManager()) {
            action(true)
        } else if (null == mActivityResult) {
            throw IllegalArgumentException("ActivityResult launcher is null,you should call registerActivityResult first")
        } else {
            if (fragment.shouldShowRequestPermissionRationale(permission)) {
                //TODO
            }

            val live = mActivityResultData.distinctUntilChanged()
            live.observe(fragment, object : Observer<ActivityResult?> {
                override fun onChanged(result: ActivityResult?) {
                    live.removeObserver(this)
                    action(Environment.isExternalStorageManager())
                }
            })
            mActivityResult?.launch(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
        }
    }

    fun <D> watchState(fragment: Fragment, rv: RecyclerView?, contentAdapter: AppListAdapter<D>) {
        stateLiveData.observe(fragment, Observer {
            it ?: return@Observer
            when (it.first) {
                DataLoad.LOADING -> {
                    rv?.adapter = SingleAdapter(R.layout.loading)
                }

                DataLoad.EMPTY -> {
                    rv?.adapter = SingleAdapter(R.layout.empty)
                }

                DataLoad.STARTED -> {
                    contentAdapter.clear()
                    rv?.adapter = contentAdapter
                }
            }
        })
    }

    fun openSetting(fragment: Fragment) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", fragment.context?.packageName, null)
        intent.data = uri
        fragment.startActivityForResult(intent, 123)
    }

    /**
     * Register a permission launcher,Call it at fragment attached.This is very first step when work with withPermission.
     *
     * @see withPermission
     */
    fun registerPermission(fragment: Fragment) {
        mPermission?.let { return }
        val permission = ActivityResultContracts.RequestPermission()
        mPermission = fragment.registerForActivityResult(permission) {
            mPermissionData.value = true == it
        }

        registerObserver(fragment, mPermission)
    }

    fun registerActivityResult(fragment: Fragment) {
        mActivityResult?.let { return }
        val permission = ActivityResultContracts.StartActivityForResult()
        mActivityResult = fragment.registerForActivityResult(permission) {
            mActivityResultData.value = it
        }

        registerObserver(fragment, mActivityResult)
    }

    private inline fun <reified T> registerObserver(
        fragment: Fragment,
        launcher: ActivityResultLauncher<T>?
    ) {
        fragment.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event.targetState == Lifecycle.State.DESTROYED) {
                    launcher?.unregister()
                    fragment.lifecycle.removeObserver(this)
                }
            }
        })
    }
}