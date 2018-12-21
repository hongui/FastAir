package com.mob.lee.fastair.fragment

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.Adapter
import com.mob.lee.fastair.adapter.ContentPickAdapter
import com.mob.lee.fastair.adapter.SimgleDataHolder
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.utils.updateStorage
import com.mob.lee.fastair.viewmodel.FileViewModel
import kotlinx.android.synthetic.main.fragment_content_pick.*
import java.io.File

/**
 * Created by Andy on 2017/6/20.
 */
class ContentPickFragment : AppFragment() {
    val PERMISSION_CODE = 12
    val INTENT_CODE = 123

    lateinit var mFileViewModel : FileViewModel
    lateinit var mAdapter:Adapter

    companion object {
        fun nav(pos : Int) : AppFragment {
            val bundle = Bundle()
            bundle.putInt("pos", pos)

            val fragment = ContentPickFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun layout() : Int = R.layout.fragment_content_pick

    override fun setting() {
        setHasOptionsMenu(true)

        val adapter = Adapter(ContentPickAdapter(),SimgleDataHolder(layout = R.layout.loading))

        pickContent.layoutManager = LinearLayoutManager(context)
        pickContent.adapter = adapter

        mFileViewModel = ViewModelProviders.of(activity !!).get(FileViewModel::class.java)
        mFileViewModel.record.observe({ lifecycle }) {
            adapter.remove(R.layout.loading)
            it?.let {
                adapter.remove(R.layout.empty)
                adapter.change(it)
            }?:let {
                if(0==adapter.itemCount) {
                    adapter.add(SimgleDataHolder(layout = R.layout.empty))
                }
            }
        }

        mFileViewModel.needUpdate.observe({ lifecycle }) {
            if(it) {
                adapter.clearAll()
            }
        }

        view?.viewTreeObserver?.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                if(userVisibleHint) {
                    view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    permisionCheck()
                }
            }
        })
    }

    override fun onPrepareOptionsMenu(menu : Menu?) {
        super.onPrepareOptionsMenu(menu)
        val allMenu = menu?.findItem(R.id.menu_content_all)
        val swapMenu = menu?.findItem(R.id.menu_content_swap)
        allMenu?.title = if (mFileViewModel.mCheckAll) {
            getString(R.string.selectAll)
        } else {
            getString(R.string.unSelectAll)
        }
        swapMenu?.title = if (mFileViewModel.mIsDes) {
            getString(R.string.des)
        } else {
            getString(R.string.des)
        }
    }

    override fun onCreateOptionsMenu(menu : Menu?, inflater : MenuInflater?) {
        inflater?.inflate(R.menu.menu_pick, menu)
    }

    override fun onOptionsItemSelected(item : MenuItem?) : Boolean {
        val pos=arguments?.getInt("pos")?:0
        when (item?.itemId) {
            R.id.menu_content_delete -> {
                val records = mFileViewModel.checkedrecords()
                if (records.isEmpty()) {
                    toast("还没有选择任何文件")
                    true
                }
                showDialog(getString(R.string.deleteTips), { dialog, which ->
                    deleteFiles(records)
                }, getString(R.string.delete), negative = getString(R.string.giveUp))
            }
            R.id.menu_content_swap -> {
                mFileViewModel.reverse(mScope,pos)
                mFileViewModel.mIsDes = ! mFileViewModel.mIsDes
                if (mFileViewModel.mIsDes) {
                    item.title = getString(R.string.des)
                } else {
                    item.title = getString(R.string.aes)
                }
            }
            R.id.menu_content_sort_byname -> {
                mFileViewModel.sortBy(mScope,pos,{it.name})
            }
            R.id.menu_content_sort_bysize -> {
                mFileViewModel.sortBy(mScope,pos,{it.size})
            }
            R.id.menu_content_sort_bytime -> {
                mFileViewModel.sortBy(mScope,pos,{it.date})
            }
            R.id.menu_content_all -> {
                //selectAll()
                if (mFileViewModel.mCheckAll) {
                    item.title = getString(R.string.selectAll)
                } else {
                    item.title = getString(R.string.unSelectAll)
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode : Int, permissions : Array<out String>, grantResults : IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PERMISSION_CODE == requestCode && ! grantResults.isEmpty()) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                load()
            } else if (shouldShowRequestPermissionRationale(permissions[0])) {
                showDialog(getString(R.string.viewTips), { dialog, which ->
                    openSetting()
                },
                        getString(R.string.goTurnOn))
            }
        }
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (INTENT_CODE == requestCode) {
            permisionCheck()
        }
    }

    fun permisionCheck() {
        mParent?:return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = ContextCompat.checkSelfPermission(mParent!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                load()
            } else {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE)
            }
        } else {
            load()
        }
    }

    fun load() {
        val pos = arguments?.getInt("pos") ?: 0
        mFileViewModel.load(mScope, context !!, pos)
    }

    fun openSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", (mParent as Context).packageName, null)
        intent.data = uri
        startActivityForResult(intent, INTENT_CODE)
    }

    fun deleteFiles(data : List<Record>) {
        val adapter = pickContent.adapter as ContentPickAdapter
        var count = 0
        data.dropLastWhile {
            val file = File(it.path)
            val success = file.delete()
            if (success) {
                updateStorage(context !!, it)
                count ++
                //mDataHolder.delete(it)
            }
            success
        }
        toast("共成功删除${count}个文件")
    }
}
