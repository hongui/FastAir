package com.mob.lee.fastair.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mob.lee.fastair.fragment.ContentPickFragment
import com.mob.lee.fastair.repository.StorageDataSource

class PageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    val pagers = StorageDataSource.categories()
    override fun getItemCount() = pagers.size

    override fun createFragment(position: Int): Fragment {
        return ContentPickFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
        }
    }

    companion object {
        fun bind(fragment: Fragment, viewPager: ViewPager2?, tabs: TabLayout?) {
            viewPager?:return
            tabs?:return
            val adapter = PageAdapter(fragment)
            viewPager.adapter = adapter

            val mediator = TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = fragment.getString(adapter.pagers[position].title)
            }
            mediator.attach()
        }
    }
}