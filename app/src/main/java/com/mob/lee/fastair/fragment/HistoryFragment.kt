package com.mob.lee.fastair.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.RecordAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.viewmodel.TransferViewModel

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
        val rv_recyclerview=view<RecyclerView>(R.id.rv_recyclerview)
        rv_recyclerview?.layoutManager = LinearLayoutManager(context)
        rv_recyclerview?.adapter = adapter

        viewModel.histories(mParent).observe {
            adapter.add(it.data)
        }
    }
}