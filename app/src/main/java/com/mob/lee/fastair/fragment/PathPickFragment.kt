package com.mob.lee.fastair.fragment

import android.os.Environment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.Adapter
import com.mob.lee.fastair.adapter.MultiDataHolder
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.model.formatDate
import com.mob.lee.fastair.utils.display
import com.mob.lee.fastair.utils.errorToast
import com.mob.lee.fastair.utils.getPaths
import com.mob.lee.fastair.utils.readDownloadPath
import com.mob.lee.fastair.utils.successToast
import com.mob.lee.fastair.viewmodel.PathPickViewModel
import kotlinx.android.synthetic.main.fragment_path_pick.*
import java.io.File


/**
 * Created by Andy on 2018/1/20.
 */
class PathPickFragment:AppFragment(){

    override val layout: Int= R.layout.fragment_path_pick

    val viewModel by lazy {
        viewModel<PathPickViewModel>()
    }
    override fun setting() {
        setHasOptionsMenu(true)
        title(R.string.choose_save_path)

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            pathPickContent.layoutManager=LinearLayoutManager(context)
            pathPickContent.adapter=(Adapter(MultiDataHolder<File>(R.layout.item_path){position, data, viewHolder ->
                data?:return@MultiDataHolder
                val icon=viewHolder.view<ImageView>(R.id.item_path_icon)
                icon?.let {
                    display(icon.context, data.path,it)
                }
                viewHolder.text(R.id.item_path_name,data.name)
                viewHolder.text(R.id.item_path_extra,data.lastModified().formatDate())
                viewHolder.itemView.setOnClickListener {
                    viewModel.updatePath(mParent,data)
                }
            }))
            pathPickTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                    tab?.position?.let { viewModel.mCurrentPositionLiveData.value=it }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.position?.let { viewModel.mCurrentPositionLiveData.value=it }
                }

            })
            viewModel.updatePath(mParent)
        } else {
            mParent?.errorToast(R.string.storage_not_ready)
            mParent?.onBackPressed()
        }

        observe(viewModel.mCurrentPositionLiveData){
            if(it==pathPickTab.tabCount) {
                val path=viewModel.
                pathPickTab.addTab(pathPickTab.newTab().setText(mCurrentPath?.getName() ?: "主目录").setContentDescription(mCurrentPath?.getAbsolutePath()?:Environment.getExternalStorageDirectory().absolutePath))
            }else{
                for (i in pathPickTab.getTabCount() - 1 downTo position + 1) {
                    pathPickTab.removeTabAt(i)
                }
            }
            pathPickTab.setScrollPosition(pathPickTab.tabCount-1, 0F, true)
        }

        observe(viewModel.pathLiveData){
            val adapter=pathPickContent.adapter as Adapter
            adapter.clearAndAdd(listOf(it))
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

}