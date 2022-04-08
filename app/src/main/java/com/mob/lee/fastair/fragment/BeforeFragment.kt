package com.mob.lee.fastair.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.FileAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.imageloader.DisplayManager
import com.mob.lee.fastair.p2p.P2PManager
import com.mob.lee.fastair.viewmodel.BeforeViewModel
import kotlinx.android.synthetic.main.before_fragment.*

class BeforeFragment : AppFragment() {
    override val layout: Int = R.layout.before_fragment
    val viewModel by lazy {
        viewModel<BeforeViewModel>()
    }

    override fun setting() {
        title(R.string.wait_for_transport_file)

        val displayManager = DisplayManager(this)
        displayManager.bindRecyclerView(rv_before_content)

        rv_before_content.layoutManager = LinearLayoutManager(mParent)
        viewModel.waitRecords(mParent).observe{ data ->
            rv_before_content.adapter = FileAdapter(displayManager) {
                viewModel.toggle(it)
            }.apply { add(data.data) }
        }

        btn_before_action.setOnClickListener {
            viewModel.submit(mParent).observe {
                P2PManager.withConnectNavigation(this,R.id.transferFragment){
                    putInt("target",R.id.transferFragment)
                }
            }
        }
    }
}