package com.mob.lee.fastair.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mob.lee.fastair.base.AppActivity
import com.mob.lee.fastair.fragment.ContentPickFragment
import com.mob.lee.fastair.repository.RecordRep

class PageAdapter(val activity : AppActivity,fm:FragmentManager):FragmentStatePagerAdapter(fm){
    val pagers= RecordRep.categories()

    override fun getItem(position : Int) : Fragment {
       return ContentPickFragment.nav(position)
    }

    override fun getCount() =pagers.size

    override fun getPageTitle(position : Int) : CharSequence? {
        val category=pagers[position]
        return activity.getString(category.title)
    }
}