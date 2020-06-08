package com.mob.lee.fastair.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.PageAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.p2p.P2PManager
import com.mob.lee.fastair.service.FileService
import com.mob.lee.fastair.utils.dialog
import com.mob.lee.fastair.utils.successToast
import com.mob.lee.fastair.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * Created by Andy on 2017/6/7.
 */
class HomeFragment : AppFragment(), NavigationView.OnNavigationItemSelectedListener {
    val PERMISSION_CODE = 12
    val INTENT_CODE = 123

    val viewModel by lazy {
        viewModel<HomeViewModel>()
    }

    override val layout: Int = R.layout.fragment_home

    override val defaultContainer: Int=R.layout.container_notoolbar

    override fun setting() {
        val toggle = ActionBarDrawerToggle(mParent!!, homeDrawer, toolbar, R.string.toggle_open, R.string.toggle_close)
        toolbar?.title = getString(R.string.app_description)
        toggle.syncState()

        homeDrawer?.addDrawerListener(toggle)
        homeNavgation?.setNavigationItemSelectedListener(this)
        homeDrawer?.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                val item = homeNavgation.menu.findItem(R.id.menu_disconnet)
                val title = if (P2PManager.isConnected()) {
                    R.string.device_disconnect
                } else {
                    R.string.device_connect
                }
                item.setTitle(title)
            }
        })

        homeContent.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                viewModel.updateLocation(mParent,position)
            }
        })

        observe(viewModel.hasSelectedLiveData){
            val id = if (true == it) {
                R.drawable.ic_action_send
            } else {
                R.drawable.ic_action_receive
            }
            toolOperation.setImageDrawable(ContextCompat.getDrawable(mParent!!, id))
        }
        toolOperation.setImageDrawable(ContextCompat.getDrawable(mParent!!, R.drawable.ic_action_receive))

        toolSwap.setOnClickListener {
            viewModel.reverse()
            val textId = if (viewModel.isDes) {
                R.string.des
            } else {
                R.string.aes
            }
            toolSwap.setText(textId)
        }
        toolAll.setOnClickListener {
            viewModel.selectAll()
            val textId = if (viewModel.checkedRecords().isNotEmpty()) {
                R.string.unSelectAll
            } else {
                R.string.selectAll
            }
            toolAll.setText(textId)
        }
        toolSort.setOnClickListener {
            val menus = PopupMenu(mParent!!, toolSort)
            menus.inflate(R.menu.menu_sort)
            menus.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_content_sort_byname -> viewModel.sortByName()
                    R.id.menu_content_sort_bysize -> viewModel.sortBySize()
                    R.id.menu_content_sort_bytime -> viewModel.sortByDate()
                }
                true
            }
            menus.show()
        }
        toolDelete.setOnClickListener {
            if (viewModel.hasSelectedLiveData.value != true) {
                mParent?.successToast(R.string.dontSelect)
                return@setOnClickListener
            }
            mParent?.dialog {
                setMessage(R.string.deleteTips)
                        .setPositiveButton(R.string.delete) { dialog, which ->
                            viewModel.delete(mParent)
                        }
            }
        }

        PageAdapter.bind(this,homeContent,homeTabs)

        permissionCheck()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var data: Bundle? = null
        when (item.getItemId()) {
            R.id.menu_disconnet -> {
                if (P2PManager.isConnected()) {
                    mParent?.dialog {
                        setMessage(R.string.msg_disconnect)
                                .setPositiveButton(R.string.stopAndDisconnect) { _, _ ->
                                    P2PManager.stopConnect(mParent!!)
                                    P2PManager.connected.value = null
                                    mParent?.stopService(Intent(mParent, FileService::class.java))
                                    mParent?.supportFinishAfterTransition()
                                }
                                .setNegativeButton(R.string.onlyDisconnect) { dialog, which ->
                                    mParent?.stopService(Intent(mParent, FileService::class.java))
                                }
                    }
                } else {
                    navigation(R.id.discoverFragment)
                }
            }

            R.id.menu_history -> {
                navigation(R.id.historyFragment) {
                    putBoolean("isHistory", true)
                }
            }

            R.id.menu_connect_chat -> navigation(R.id.chatFragment) {
                putAll(P2PManager.bundle())
            }

            R.id.menu_payment -> navigation(R.id.payFragment)

            R.id.menu_help -> navigation(R.id.textFragment) {
                putInt("type", 0)
            }

            R.id.menu_about -> navigation(R.id.textFragment) {
                putInt("type", 1)
            }

            R.id.menu_setting -> navigation(R.id.settingFragment)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (INTENT_CODE == requestCode) {
            permissionCheck()
        }
    }

    fun permissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = ContextCompat.checkSelfPermission(mParent!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permission == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE)
            } else {
                viewModel.updateLocation(mParent,0)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PERMISSION_CODE == requestCode && grantResults.isNotEmpty()) {
            if (shouldShowRequestPermissionRationale(permissions[0])) {
                mParent?.dialog {
                    setMessage(R.string.viewTips)
                            .setPositiveButton(R.string.goTurnOn) { _, _ ->
                                openSetting()
                            }
                }
            }
        }
    }

    fun openSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", mParent?.packageName, null)
        intent.data = uri
        startActivityForResult(intent, INTENT_CODE)
    }
}
