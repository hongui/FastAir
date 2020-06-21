package com.mob.lee.fastair.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.FileAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.utils.successToast
import com.mob.lee.fastair.viewmodel.BeforeViewModel
import kotlinx.android.synthetic.main.before_fragment.*

class BeforeFragment : AppFragment() {
    override val layout: Int = R.layout.before_fragment
    val viewModel by lazy {
        viewModel<BeforeViewModel>()
    }

    override fun setting() {
        title(R.string.wait_for_transport_file)

        rv_before_content.layoutManager = LinearLayoutManager(mParent)
        observe(viewModel.waitRecords(mParent)) { data ->
            rv_before_content.adapter = FileAdapter {
                viewModel.toggle(it)
            }.apply { add(data.data) }
        }

        btn_before_action.setOnClickListener {
            observe(viewModel.submit(mParent)) {
                navigation(R.id.transferFragment)
            }
        }
    }
}