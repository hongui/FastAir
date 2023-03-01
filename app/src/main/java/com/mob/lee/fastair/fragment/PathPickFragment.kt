package com.mob.lee.fastair.fragment

import android.os.Environment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.AppListAdapter
import com.mob.lee.fastair.adapter.AppViewHolder
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.model.formatDate
import com.mob.lee.fastair.utils.errorToast
import com.mob.lee.fastair.viewmodel.PathPickViewModel
import java.io.File


/**
 * Created by Andy on 2018/1/20.
 */
class PathPickFragment : AppFragment() {

    override val layout: Int = R.layout.fragment_path_pick

    val viewModel by lazy {
        viewModel<PathPickViewModel>()
    }

    override fun setting() {
        setHasOptionsMenu(true)
        title(R.string.choose_save_path)

        val adapter = PathAdapter()
        val pathPickContent=view<RecyclerView>(R.id.pathPickContent)
        val pathPickTab=view<TabLayout>(R.id.pathPickTab)
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            pathPickContent?.layoutManager = LinearLayoutManager(context)
            pathPickContent?.adapter = adapter
            viewModel.updatePath(mParent, pos = 0)
        } else {
            mParent?.errorToast(R.string.storage_not_ready)
            mParent?.onBackPressed()
        }

        pathPickTab?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewModel.updatePath(mParent, pos = tab?.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.updatePath(mParent, pos = tab?.position)
            }

        })

        viewModel.watchState(this, pathPickContent, adapter)
        viewModel.currentPositionLiveData.observe {
            it ?: return@observe
            if (it == pathPickTab?.tabCount) {
                val path = viewModel.currentPath
                pathPickTab.addTab(pathPickTab.newTab().setText(if (0 == it) {
                    getString(R.string.home_dir)
                } else {
                    path?.getName() ?: getString(R.string.home_dir)
                }))
            } else {
                val tabCount=(pathPickTab?.getTabCount()?:1) - 1
                for (i in tabCount downTo it + 1) {
                    pathPickTab?.removeTabAt(i)
                }
            }
            pathPickTab?.setScrollPosition(pathPickTab.tabCount - 1, 0F, true)
        }

        viewModel.pathLiveData.observe {
            adapter.clear()
            adapter.add(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_ok, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == R.id.menu_ok) {
            viewModel.submit(this)
        }
        return super.onOptionsItemSelected(item)
    }

    inner class PathAdapter : AppListAdapter<File>(R.layout.item_path) {
        override fun onBindViewHolder(holder: AppViewHolder, position: Int, data: File) {
            holder.text(R.id.item_path_name, data.name)
            holder.text(R.id.item_path_extra, data.lastModified().formatDate())
            holder.itemView.setOnClickListener {
                viewModel.updatePath(mParent, data)
            }
        }
    }
}