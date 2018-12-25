package com.mob.lee.fastair.fragment

import android.graphics.Color
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.Adapter
import com.mob.lee.fastair.adapter.MultiDataHolder
import com.mob.lee.fastair.adapter.SimgleDataHolder
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.model.*
import com.mob.lee.fastair.utils.display
import com.mob.lee.fastair.utils.updateStorage
import com.mob.lee.fastair.viewmodel.FileViewModel
import kotlinx.android.synthetic.main.fragment_content_pick.*
import java.io.File

/**
 * Created by Andy on 2017/6/20.
 */
class ContentPickFragment : AppFragment() {
    override fun layout(): Int = R.layout.fragment_content_pick

    override fun setting() {
        val viewModel = ViewModelProviders.of(mParent!!).get(FileViewModel::class.java)

        val contentDataHolder = MultiDataHolder<Record>(R.layout.item_file, { position, record, viewHolder ->
            record ?: return@MultiDataHolder
            viewHolder.text(R.id.item_file_name, record.name)
            viewHolder.text(R.id.item_file_extra, record.date.formatDate() + "\t\t" + record.size.formatSize(viewHolder.itemView.context))
            val iconView = viewHolder.view<ImageView>(R.id.item_file_icon)
            val checkBox = viewHolder.view<CheckBox>(R.id.item_file_selector)
            if (null != iconView) {
                display(viewHolder.itemView.context, record.path, iconView)
            }

            viewHolder.itemView.setOnClickListener {
                val state = if (checkBox?.isChecked ?: false) {
                    STATE_ORIGIN
                } else {
                    STATE_CHECK
                }
                viewModel.updateState(mScope, state, viewHolder.adapterPosition, 1)
            }
            viewHolder.check(R.id.item_file_selector, STATE_CHECK == record.state)
            val isChecked = viewHolder.view<CheckBox>(R.id.item_file_selector)?.isChecked ?: false
            val color = if (isChecked) {
                ContextCompat.getColor(viewHolder.itemView.context, R.color.material_grey_300)
            } else {
                Color.WHITE
            }
            viewHolder.itemView.setBackgroundColor(color)
        })
        val adapter = Adapter(contentDataHolder)

        pickContent.layoutManager = LinearLayoutManager(context)
        pickContent.adapter = adapter

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

        viewModel.update.observe({ lifecycle }) {
            adapter.change(it.second,it.first)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val allMenu = menu?.findItem(R.id.menu_content_all)
        val swapMenu = menu?.findItem(R.id.menu_content_swap)
        val viewModel = ViewModelProviders.of(activity!!).get(FileViewModel::class.java)
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_pick, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val viewModel = ViewModelProviders.of(activity!!).get(FileViewModel::class.java)

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
                viewModel.mIsDes = !viewModel.mIsDes
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

    fun deleteFiles(data: List<Record>) {
        val adapter = pickContent.adapter
        var count = 0
        data.dropLastWhile {
            val file = File(it.path)
            val success = file.delete()
            if (success) {
                updateStorage(context!!, it)
                count++
                //mDataHolder.delete(it)
            }
            success
        }
        toast("共成功删除${count}个文件")
    }
}
