package com.mob.lee.fastair.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.RecordAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.viewmodel.TransferViewModel
import kotlinx.android.synthetic.main.fragment_recyclerview.*

/**
 * Created by Andy on 2017/8/31.
 */
class HistoryFragment : AppFragment() {
    override val layout: Int = R.layout.fragment_recyclerview
    val viewModel by lazy {
        viewModel<TransferViewModel>()
    }

    override fun setting() {
        title(R.string.transfer_history)

        val adapter = RecordAdapter {
            viewModel.rename(requireContext(), it)
        }
        rv_recyclerview?.layoutManager = LinearLayoutManager(context)
        rv_recyclerview?.adapter = adapter

        observe(viewModel.histories(mParent)) {
            adapter.add(it.data)
        }
    }
}