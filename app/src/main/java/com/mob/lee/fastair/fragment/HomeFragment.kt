package com.mob.lee.fastair.fragment

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.DeleteAdapter
import com.mob.lee.fastair.adapter.PageAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.service.FileService
import com.mob.lee.fastair.utils.dialog
import com.mob.lee.fastair.utils.errorToast
import com.mob.lee.fastair.utils.successToast
import com.mob.lee.fastair.viewmodel.DeviceViewModel
import com.mob.lee.fastair.viewmodel.HomeViewModel

/**
 * Created by Andy on 2017/6/7.
 */
class HomeFragment : AppFragment(), NavigationView.OnNavigationItemSelectedListener {

    override val layout: Int = R.layout.fragment_home

    override val defaultContainer: Int = -1

    val mDeviceViewModel by lazy {
        activityViewModel<DeviceViewModel>()
    }

    val mViewModel by lazy {
        activityViewModel<HomeViewModel>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 30) {
            mViewModel.registerActivityResult(this)
        }
        mViewModel.registerPermission(this)
    }

    override fun setting() {
        val homeDrawer = view<DrawerLayout>(R.id.homeDrawer)
        val toolbar = view<Toolbar>(R.id.toolbar)
        val homeNavgation = view<NavigationView>(R.id.homeNavgation)
        val homeContent = view<ViewPager2>(R.id.homeContent)
        val homeTabs = view<TabLayout>(R.id.homeTabs)
        val toolOperation = view<FloatingActionButton>(R.id.toolOperation)
        val toolSwap = view<AppCompatTextView>(R.id.toolSwap)
        val toolAll = view<AppCompatTextView>(R.id.toolAll)
        val toolSort = view<AppCompatTextView>(R.id.toolSort)
        val toolDelete = view<AppCompatTextView>(R.id.toolDelete)
        val toggle = ActionBarDrawerToggle(
            mParent!!,
            homeDrawer,
            toolbar,
            R.string.toggle_open,
            R.string.toggle_close
        )
        toolbar?.title = getString(R.string.app_description)
        toggle.syncState()

        homeDrawer?.addDrawerListener(toggle)
        homeNavgation?.setNavigationItemSelectedListener(this)
        homeDrawer?.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                val item = homeNavgation?.menu?.findItem(R.id.menu_disconnet)
                val title = if (mDeviceViewModel.isConnected()) {
                    R.string.device_disconnect
                } else {
                    R.string.device_connect
                }
                item?.setTitle(title)
            }
        })

        mViewModel.hasSelectedLiveData.observe {
            val value = if (true == it) {
                R.drawable.ic_action_file_upload to R.color.color_red
            } else {
                R.drawable.ic_action_file_download to R.color.colorAccent
            }
            toolOperation?.supportBackgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), value.second))
            toolOperation?.setImageDrawable(ContextCompat.getDrawable(mParent!!, value.first))
        }
        toolOperation?.setImageDrawable(
            ContextCompat.getDrawable(
                mParent!!,
                R.drawable.ic_action_file_download
            )
        )

        toolSwap?.setOnClickListener {
            mViewModel.reverse()
            val textId = if (mViewModel.isDes) {
                R.string.des
            } else {
                R.string.aes
            }
            toolSwap.setText(textId)
        }
        toolAll?.setOnClickListener {
            mViewModel.selectAll()
            val textId = if (mViewModel.checkedRecords().isNotEmpty()) {
                R.string.unselect_all
            } else {
                R.string.select_all
            }
            toolAll.setText(textId)
        }
        toolSort?.setOnClickListener {
            val menus = PopupMenu(mParent!!, toolSort)
            menus.inflate(R.menu.menu_sort)
            menus.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_content_sort_byname -> mViewModel.sortByName()
                    R.id.menu_content_sort_bysize -> mViewModel.sortBySize()
                    R.id.menu_content_sort_bytime -> mViewModel.sortByDate()
                }
                true
            }
            menus.show()
        }
        toolDelete?.setOnClickListener {
            if (mViewModel.hasSelectedLiveData.value != true) {
                mParent?.successToast(R.string.select_nothing)
                return@setOnClickListener
            }
            mParent?.dialog {
                setTitle(R.string.delete_file_list)
                val rv = layoutInflater.inflate(R.layout.recyclerview, null) as RecyclerView
                rv.layoutManager = LinearLayoutManager(mParent)
                rv.adapter = DeleteAdapter(mViewModel)
                setView(rv)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        mViewModel.delete(mParent)
                        mViewModel.update()
                    }
                    .setNegativeButton(R.string.cancel) { _, _ ->
                        mViewModel.update()
                    }
            }
        }

        toolOperation?.setOnClickListener {
            if (mViewModel.checkedRecords().isEmpty()) {
                mDeviceViewModel.withConnectNavigation(this, R.id.transferFragment) {
                    putInt("target", R.id.transferFragment)
                }
            } else {
                mViewModel.write(context).observe {
                    if (it.isSuccess()) {
                        navigation(R.id.beforeFragment)
                    } else {
                        mParent?.errorToast(it.msg)
                    }
                }
            }
        }
        PageAdapter.bind(this, homeContent, homeTabs)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.menu_disconnet -> {
                if (mDeviceViewModel.isConnected()) {
                    mParent?.dialog {
                        setMessage(R.string.disconnect_info)
                            .setPositiveButton(R.string.disconnect_now) { _, _ ->
                                val intent = Intent(context, FileService::class.java)
                                mParent?.stopService(intent)
                            }
                            .setNegativeButton(R.string.cancel, null)
                    }
                } else {
                    navigation(R.id.discoverFragment)
                }
            }
            R.id.menu_host -> {
                navigation(R.id.hostFragment)
            }

            R.id.menu_history -> {
                navigation(R.id.historyFragment, args = {
                    putBoolean("isHistory", true)
                })
            }

            R.id.menu_connect_chat -> mDeviceViewModel.withConnectNavigation(
                this,
                R.id.chatFragment
            ) {
                putAll(mDeviceViewModel.bundle())
                putInt("target", R.id.chatFragment)
            }

            R.id.menu_payment -> navigation(R.id.payFragment)

            R.id.menu_help -> navigation(R.id.textFragment, args = {
                putInt("type", 0)
            })

            R.id.menu_about -> navigation(R.id.textFragment, args = {
                putInt("type", 1)
            })

            R.id.menu_setting -> navigation(R.id.settingFragment)
        }
        val homeDrawer = view<DrawerLayout>(R.id.homeDrawer)
        homeDrawer?.closeDrawer(Gravity.LEFT)
        return true
    }

}
