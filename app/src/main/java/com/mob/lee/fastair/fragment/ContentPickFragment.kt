package com.mob.lee.fastair.fragment

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.FileAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.imageloader.DisplayManager
import com.mob.lee.fastair.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_content_pick.*

/**
 * Created by Andy on 2017/6/20.
 */
class ContentPickFragment : AppFragment() {
    override val layout: Int = R.layout.fragment_content_pick
    override val defaultContainer: Int = -1
    override fun setting() {
        val viewModel:HomeViewModel by mParent!!.viewModels()
        val displayManager=DisplayManager(this)
        displayManager.bindRecyclerView(pickContent)

        val adapter = FileAdapter(displayManager) {
            viewModel.toggleState(it)
        }

        pickContent.layoutManager = LinearLayoutManager(context).apply {
        }

        viewModel.watchState(this,pickContent,adapter)

        viewModel.recordLiveData.observe {
            it ?: return@observe
            adapter.add(it)
        }
    }

}
