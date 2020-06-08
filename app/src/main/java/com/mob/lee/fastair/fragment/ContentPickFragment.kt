package com.mob.lee.fastair.fragment

import android.graphics.Color
import android.widget.CheckBox
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.Adapter
import com.mob.lee.fastair.adapter.FileDetailAdapter
import com.mob.lee.fastair.adapter.MultiDataHolder
import com.mob.lee.fastair.adapter.SimgleDataHolder
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.model.Record
import com.mob.lee.fastair.model.formatDate
import com.mob.lee.fastair.model.formatSize
import com.mob.lee.fastair.utils.dialog
import com.mob.lee.fastair.utils.display
import com.mob.lee.fastair.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_content_pick.*
import java.io.File

/**
 * Created by Andy on 2017/6/20.
 */
class ContentPickFragment : AppFragment() {
    override val layout: Int = R.layout.fragment_content_pick
    override val defaultContainer: Int = R.layout.container_notoolbar
    override fun setting() {
        val viewModel = (parentFragment as AppFragment).viewModel<HomeViewModel>()

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
                record.state = if (checkBox?.isChecked ?: false) {
                    Record.STATE_ORIGIN
                } else {
                    Record.STATE_CHECK
                }
            }
            viewHolder.itemView.setOnLongClickListener {
                context?.dialog {
                    setTitle(record.path.substringAfterLast(File.separator))
                            .setAdapter(FileDetailAdapter(mParent!!, record.path), null)
                }
                true
            }
            viewHolder.check(R.id.item_file_selector, Record.STATE_CHECK == record.state)
            val isChecked = viewHolder.view<CheckBox>(R.id.item_file_selector)?.isChecked ?: false
            val color = if (isChecked) {
                ContextCompat.getColor(viewHolder.itemView.context, R.color.material_grey_300)
            } else {
                Color.WHITE
            }
            viewHolder.itemView.setBackgroundColor(color)
        }
        val adapter = Adapter(SimgleDataHolder(layout = R.layout.empty))
        adapter.add(SimgleDataHolder(layout = R.layout.empty))

        pickContent.layoutManager = LinearLayoutManager(context)
        pickContent.adapter = adapter

        observe(viewModel.recordLiveData) {
            adapter.remove(R.layout.loading)
            adapter.change(it)
        }
    }
}
