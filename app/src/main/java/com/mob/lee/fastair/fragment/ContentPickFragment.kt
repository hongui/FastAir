package com.mob.lee.fastair.fragment

import android.graphics.Color
import android.widget.CheckBox
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.Adapter
import com.mob.lee.fastair.adapter.MultiDataHolder
import com.mob.lee.fastair.adapter.SimgleDataHolder
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.model.STATE_CHECK
import com.mob.lee.fastair.model.STATE_ORIGIN
import com.mob.lee.fastair.model.formatDate
import com.mob.lee.fastair.model.formatSize
import com.mob.lee.fastair.utils.display
import com.mob.lee.fastair.viewmodel.FileViewModel
import kotlinx.android.synthetic.main.fragment_content_pick.*

/**
 * Created by Andy on 2017/6/20.
 */
class ContentPickFragment : AppFragment() {
    override fun layout(): Int = R.layout.fragment_content_pick

    override fun setting() {
        val viewModel = ViewModelProviders.of(mParent!!).get(FileViewModel::class.java)

        val contentDataHolder = MultiDataHolder<Record>(R.layout.item_file) { position, record, viewHolder ->
            record ?: return@MultiDataHolder
            viewHolder.text(R.id.item_file_name, record.name)
            viewHolder.text(R.id.item_file_extra, record.date.formatDate() + "\t\t" + record.size.formatSize(viewHolder.itemView.context))
            val iconView = viewHolder.view<ImageView>(R.id.item_file_icon)
            val checkBox = viewHolder.view<CheckBox>(R.id.item_file_selector)
            if (null != iconView) {
                display(viewHolder.itemView.context, record.path, iconView)
            }

            viewHolder.itemView.setOnClickListener {
                val state = if (checkBox?.isChecked ?: false) {
                    STATE_ORIGIN
                } else {
                    STATE_CHECK
                }
                viewModel.updateState(mScope, state, viewHolder.adapterPosition, 1)
            }
            viewHolder.check(R.id.item_file_selector, STATE_CHECK == record.state)
            val isChecked = viewHolder.view<CheckBox>(R.id.item_file_selector)?.isChecked ?: false
            val color = if (isChecked) {
                ContextCompat.getColor(viewHolder.itemView.context, R.color.material_grey_300)
            } else {
                Color.WHITE
            }
            viewHolder.itemView.setBackgroundColor(color)
        }
        val adapter = Adapter(contentDataHolder)

        pickContent.layoutManager = LinearLayoutManager(context)
        pickContent.adapter = adapter

        viewModel.record.observe({ lifecycle }) {
            it?.let {
                adapter.change(it)
            }
        }

        viewModel.state.observe({ lifecycle }) {
            when (it) {
                FileViewModel.STATE_PRE -> {
                    adapter.remove(R.layout.empty)
                    adapter.clearAll()
                    adapter.add(SimgleDataHolder(layout = R.layout.loading))
                }

                FileViewModel.STATE_START -> {
                    adapter.remove(R.layout.loading)
                }

                FileViewModel.STATE_FAILED -> {
                    adapter.remove(R.layout.loading)
                    adapter.add(SimgleDataHolder(layout = R.layout.empty))
                }
            }
        }

        viewModel.update.observe({ lifecycle }) {
            adapter.change(it.second,it.first)
        }
    }
}
