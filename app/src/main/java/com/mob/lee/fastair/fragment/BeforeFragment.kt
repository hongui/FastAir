package com.mob.lee.fastair.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.FileAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.viewmodel.BeforeViewModel
import com.mob.lee.fastair.viewmodel.DeviceViewModel

class BeforeFragment : AppFragment() {
    override val layout: Int = R.layout.before_fragment
    val viewModel by lazy {
        viewModel<BeforeViewModel>()
    }

    override fun setting() {
        title(R.string.wait_for_transport_file)

        view<RecyclerView>(R.id.rv_before_content)?.run {
            layoutManager = LinearLayoutManager(mParent)
            viewModel.waitRecords(mParent).observe { data ->
                adapter = FileAdapter {
                    viewModel.toggle(it)
                }.apply { add(data.data) }
            }
        }


        view<View>(R.id.btn_before_action)?.setOnClickListener {
            viewModel.submit(mParent).observe {
                activityViewModel<DeviceViewModel>().withConnectNavigation(
                    this,
                    R.id.transferFragment
                ) {
                    putInt("target", R.id.transferFragment)
                }
            }
        }
    }
}