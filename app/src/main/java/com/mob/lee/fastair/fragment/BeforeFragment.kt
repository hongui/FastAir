package com.mob.lee.fastair.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.FileAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.viewmodel.BeforeViewModel
import kotlinx.android.synthetic.main.recyclerview.*

class BeforeFragment :AppFragment(){
    override val layout: Int=R.layout.recyclerview
    val viewModel by lazy {
        viewModel<BeforeViewModel>()
    }
    override fun setting() {
        title(R.string.wait_for_transport_file)

        recycler_view.layoutManager = LinearLayoutManager(mParent)
        observe(viewModel.files(mParent)){data->
            recycler_view.adapter=FileAdapter().apply { add(data.data) }
        }
    }
}