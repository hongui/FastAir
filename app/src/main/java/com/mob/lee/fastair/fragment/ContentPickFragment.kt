package com.mob.lee.fastair.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.FileAdapter
import com.mob.lee.fastair.adapter.SingleAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.model.DataLoad
import com.mob.lee.fastair.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_content_pick.*

/**
 * Created by Andy on 2017/6/20.
 */
class ContentPickFragment : AppFragment() {
    override val layout: Int = R.layout.fragment_content_pick
    override val defaultContainer: Int = -1
    override fun setting() {
        val viewModel = (parentFragment as AppFragment).viewModel<HomeViewModel>()

        val adapter=FileAdapter()
        pickContent.layoutManager = LinearLayoutManager(context)

        viewModel.stateLiveData.observe(this, Observer {
            it?:return@Observer
            when (it.first) {
                DataLoad.LOADING -> {
                    pickContent.adapter=SingleAdapter(R.layout.loading)
                }

                DataLoad.EMPTY -> {
                    pickContent.adapter=SingleAdapter(R.layout.empty)
                }

                DataLoad.STARTED -> {
                    adapter.clear()
                    pickContent.adapter=adapter
                }
            }
        })

        observe(viewModel.recordLiveData) {
            adapter.add(it)
        }
    }
}
