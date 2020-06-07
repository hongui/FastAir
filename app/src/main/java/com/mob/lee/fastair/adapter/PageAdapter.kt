package com.mob.lee.fastair.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.mob.lee.fastair.fragment.ContentPickFragment
import com.mob.lee.fastair.repository.RecordRep

class PageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    val pagers = RecordRep.categories()
    val map = HashMap<Int, Fragment>()

    override fun getItemCount() = pagers.size

    override fun createFragment(position: Int): Fragment {
        return map.getOrPut(position, {
            ContentPickFragment().apply {
                arguments = Bundle().apply {
                    putInt("position", position)
                }
            }
        })
    }

    companion object {
        fun bind(fragment: Fragment, viewPager: ViewPager2, tab: TabLayout) {
            val adapter = PageAdapter(fragment)
            viewPager.adapter = adapter

            val mediator = TabLayoutMediator(tab, viewPager) { _, position ->
                tab.text = adapter.pagers[position]
            }
            mediator.attach()
        }
    }
}