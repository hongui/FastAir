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
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.ContentPickAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.model.FileCategory
import com.mob.lee.fastair.model.IS_SEND
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.utils.database
import com.mob.lee.fastair.utils.errorToast
import com.mob.lee.fastair.utils.updateStorage
import com.mob.lee.fastair.viewmodel.FileViewModel
import kotlinx.android.synthetic.main.fragment_content_pick.*
import java.io.File
import kotlin.concurrent.thread


/**
 * Created by Andy on 2017/6/20.
 */
class ContentPickFragment : AppFragment() {
    val PERMISSION_CODE = 12
    val INTENT_CODE = 123

    lateinit var mFileViewModel: FileViewModel

    override fun layout(): Int = R.layout.fragment_content_pick

    override fun setting() {
        setHasOptionsMenu(true)
        val fileCategory = FileCategory(arguments?.getInt("fileCategory")?:0)
        toolbar(fileCategory.title)

        val adapter = ContentPickAdapter()
        pickContent.layoutManager = LinearLayoutManager(context)
        pickContent.adapter = adapter

        pickSend.setOnClickListener {
            val records = mFileViewModel.checkedrecords()
            if (records.isEmpty()) {
                context?.errorToast("请先选择发送文件")
                return@setOnClickListener
            }
            val bundle = Bundle()
            bundle.putBoolean(IS_SEND, true)
            mParent?.fragment(DiscoverFragment::class, bundle = bundle)
            thread {
                context?.database()?.recordDao()?.insert(records)
            }
        }

        mFileViewModel = ViewModelProviders.of(activity!!).get(FileViewModel::class.java)
        mFileViewModel.mRecords.observe({ lifecycle }) {
            it?.let {
                adapter.addAll(it)
            }
        }
        pickSend.viewTreeObserver.addOnGlobalLayoutListener(object :ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                anim(false)
                pickSend.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        permisionCheck()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val allMenu = menu?.findItem(R.id.menu_content_all)
        val swapMenu = menu?.findItem(R.id.menu_content_swap)
        allMenu?.title=if(mFileViewModel.mCheckAll){
            getString(R.string.selectAll)
        }else{
            getString(R.string.unSelectAll)
        }
        swapMenu?.title=if(mFileViewModel.mIsDes){
            getString(R.string.des)
        }else{
            getString(R.string.des)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_pick, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
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
                reverse()
                mFileViewModel.mIsDes=!mFileViewModel.mIsDes
                if(mFileViewModel.mIsDes){
                    item.title = getString(R.string.des)
                }else{
                    item.title = getString(R.string.aes)
                }
            }
            R.id.menu_content_sort_byname -> {
                sortByName()
            }
            R.id.menu_content_sort_bysize -> {
                sortBySize()
            }
            R.id.menu_content_sort_bytime -> {
                sortByDate()
            }
            R.id.menu_content_all -> {
                selectAll()
                if(mFileViewModel.mCheckAll){
                    item.title = getString(R.string.selectAll)
                }else{
                    item.title = getString(R.string.unSelectAll)
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PERMISSION_CODE == requestCode && !grantResults.isEmpty()) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                mFileViewModel.load(context!!, arguments?.getInt("fileCategory")?:0)
            } else if (shouldShowRequestPermissionRationale(permissions[0])) {
                showDialog(getString(R.string.viewTips),
                        { dialog, which ->
                            openSetting()
                        },
                        getString(R.string.goTurnOn))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (INTENT_CODE == requestCode) {
            permisionCheck()
        }
    }

    fun permisionCheck() {
        val fileCategory = arguments?.getInt("fileCategory")?:0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = ContextCompat.checkSelfPermission(mParent as Context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                mFileViewModel.load(context!!, fileCategory)
            } else {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE)
            }
        } else {
            mFileViewModel.load(context!!, fileCategory)
        }
    }

    fun openSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", (mParent as Context).packageName, null)
        intent.data = uri
        startActivityForResult(intent, INTENT_CODE)
    }

    fun reverse() {
        val adapter = pickContent.adapter as ContentPickAdapter
        val list = adapter.datas.reversed()
        adapter.clearAndAdd(list)
    }

    fun sortBySize() {
        val adapter = pickContent.adapter as ContentPickAdapter
        val list = adapter.datas.sortedBy { it.size }
        adapter.clearAndAdd(list)
    }

    fun sortByName() {
        val adapter = pickContent.adapter as ContentPickAdapter
        val list = adapter.datas.sortedBy { it.name.toLowerCase() }
        adapter.clearAndAdd(list)
    }

    fun sortByDate() {
        val adapter = pickContent.adapter as ContentPickAdapter
        val list = adapter.datas.sortedBy { it.date }
        adapter.clearAndAdd(list)
    }

    fun selectAll() {
        val adapter = pickContent.adapter as ContentPickAdapter
        adapter.selectOrUnSelectAll(mFileViewModel.mCheckAll)
    }

    fun deleteFiles(data: List<Record>) {
        val adapter = pickContent.adapter as ContentPickAdapter
        var count = 0
        data.dropLastWhile {
            val file=File(it.path)
            val success = file.delete()
            if (success) {
                updateStorage(context!!, it)
                count++
                adapter.remove(it)
            }
            success
        }
        toast("共成功删除${count}个文件")
    }

    fun anim(show: Boolean) {
        if(null==pickSend){
            return
        }
        val margin = pickSend.layoutParams as ViewGroup.MarginLayoutParams
        val max = pickSend.height + margin.bottomMargin.toFloat()
        val animator = if (show) {
            ObjectAnimator.ofFloat(pickSend, "translationY", max, 0F)
        } else {
            ObjectAnimator.ofFloat(pickSend, "translationY", 0F, max)
        }
        animator.setInterpolator(FastOutSlowInInterpolator())
        animator.start()
    }
}
