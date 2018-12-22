package com.mob.lee.fastair.fragment

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
    override fun layout() : Int = R.layout.fragment_content_pick

    override fun setting() {
        val adapter = Adapter(ContentPickAdapter())

        pickContent.layoutManager = LinearLayoutManager(context)
        pickContent.adapter = adapter

        val viewModel = ViewModelProviders.of(mParent!!).get(FileViewModel::class.java)
        viewModel.record.observe({ lifecycle }) {
            it?.let {
                adapter.change(it)
            }
        }

        viewModel.state.observe({ lifecycle }) {
            when (it) {
                FileViewModel.STATE_PRE -> {
                    adapter.remove(R.layout.empty)
                    adapter.clearAll()
                    adapter.add(SimgleDataHolder(layout = R.layout.loading))
                }

                FileViewModel.STATE_START -> {
                    adapter.remove(R.layout.loading)
                }

                FileViewModel.STATE_FAILED -> {
                    adapter.remove(R.layout.loading)
                    adapter.add(SimgleDataHolder(layout = R.layout.empty))
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu : Menu?) {
        super.onPrepareOptionsMenu(menu)
        val allMenu = menu?.findItem(R.id.menu_content_all)
        val swapMenu = menu?.findItem(R.id.menu_content_swap)
        val viewModel = ViewModelProviders.of(activity !!).get(FileViewModel::class.java)
        allMenu?.title = if (viewModel.mCheckAll) {
            getString(R.string.selectAll)
        } else {
            getString(R.string.unSelectAll)
        }
        swapMenu?.title = if (viewModel.mIsDes) {
            getString(R.string.des)
        } else {
            getString(R.string.des)
        }
    }

    override fun onCreateOptionsMenu(menu : Menu?, inflater : MenuInflater?) {
        inflater?.inflate(R.menu.menu_pick, menu)
    }

    override fun onOptionsItemSelected(item : MenuItem?) : Boolean {
        val viewModel = ViewModelProviders.of(activity !!).get(FileViewModel::class.java)

        when (item?.itemId) {
            R.id.menu_content_delete -> {
                val records = viewModel.checkedrecords()
                if (records.isEmpty()) {
                    toast("还没有选择任何文件")
                    true
                }
                showDialog(getString(R.string.deleteTips), { dialog, which ->
                    deleteFiles(records)
                }, getString(R.string.delete), negative = getString(R.string.giveUp))
            }
            R.id.menu_content_swap -> {
                viewModel.reverse(mScope)
                viewModel.mIsDes = ! viewModel.mIsDes
                if (viewModel.mIsDes) {
                    item.title = getString(R.string.des)
                } else {
                    item.title = getString(R.string.aes)
                }
            }
            R.id.menu_content_sort_byname -> {
                viewModel.sortBy(mScope, { it.name })
            }
            R.id.menu_content_sort_bysize -> {
                viewModel.sortBy(mScope, { it.size })
            }
            R.id.menu_content_sort_bytime -> {
                viewModel.sortBy(mScope, { it.date })
            }
            R.id.menu_content_all -> {
                //selectAll()
                if (viewModel.mCheckAll) {
                    item.title = getString(R.string.selectAll)
                } else {
                    item.title = getString(R.string.unSelectAll)
                }
            }
        }
        return true
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
