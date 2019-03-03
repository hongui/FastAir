package com.mob.lee.fastair.fragment

import android.Manifest
import android.content.Context
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
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.SettingActivity
import com.mob.lee.fastair.adapter.PageAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.model.STATE_WAIT
import com.mob.lee.fastair.p2p.P2PManager
import com.mob.lee.fastair.repository.RecordRep
import com.mob.lee.fastair.service.FileService
import com.mob.lee.fastair.utils.database
import com.mob.lee.fastair.utils.dialog
import com.mob.lee.fastair.utils.successToast
import com.mob.lee.fastair.viewmodel.FileViewModel
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * Created by Andy on 2017/6/7.
 */
class HomeFragment : AppFragment(), NavigationView.OnNavigationItemSelectedListener {
    val PERMISSION_CODE = 12
    val INTENT_CODE = 123

    override fun layout() : Int = R.layout.fragment_home

    override fun setting() {
        homeContent?.adapter = PageAdapter(mParent !!, childFragmentManager)
        homeTabs.setupWithViewPager(homeContent)

        val toggle = ActionBarDrawerToggle(mParent !!, homeDrawer, toolbar, R.string.toggle_open, R.string.toggle_close)
        toolbar?.title = getString(R.string.app_description)
        toggle.syncState()

        homeDrawer?.addDrawerListener(toggle)
        homeNavgation?.setNavigationItemSelectedListener(this)
        homeDrawer?.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView : View) {
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

        val viewmodel = ViewModelProviders.of(mParent !!).get(FileViewModel::class.java)
        homeContent.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position : Int) {
                viewmodel.position = position
                permisionCheck()
            }
        })

        toolOperation.setImageDrawable(ContextCompat.getDrawable(context !!, R.drawable.ic_action_receive))
        viewmodel.hasSelect.observe({ lifecycle }) {
            val id = if (true == it) {
                R.drawable.ic_action_send
            } else {
                R.drawable.ic_action_receive
            }
            toolOperation.setImageDrawable(ContextCompat.getDrawable(context !!, id))
        }
        toolOperation.setOnClickListener {
            mParent?.database(mScope, { dao ->
                val records = RecordRep.selectRecords
                records.forEach {
                    it.state = STATE_WAIT
                }
                dao.insert(records)
                //不清除会造成下次重复发送
                RecordRep.selectRecords.clear()
            })
            val bundle = P2PManager.bundle()
            mParent?.fragment(HistoryFragment::class, bundle)
        }
        toolSwap.setOnClickListener {
            viewmodel.reverse(mScope)
            val textId = if (toolSwap.text == getString(R.string.des)) {
                R.string.aes
            } else {
                R.string.des
            }
            toolSwap.setText(textId)
        }
        toolAll.setOnClickListener {
            viewmodel.toggleState(mScope)
            val textId = if (toolAll.text == getString(R.string.selectAll)) {
                R.string.unSelectAll
            } else {
                R.string.selectAll
            }
            toolAll.setText(textId)
        }
        toolSort.setOnClickListener {
            val menus = PopupMenu(context !!, toolSort)
            menus.inflate(R.menu.menu_sort)
            menus.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_content_sort_byname -> viewmodel.sortBy(mScope, { it.name })
                    R.id.menu_content_sort_bysize -> viewmodel.sortBy(mScope, { it.size })
                    R.id.menu_content_sort_bytime -> viewmodel.sortBy(mScope, { it.date })
                }
                true
            }
            menus.show()
        }
        toolDelete.setOnClickListener {
            if (viewmodel.hasSelect.value != true) {
                mParent?.successToast(R.string.dontSelect)
                return@setOnClickListener
            }
            mParent?.dialog {
                it.setMessage(R.string.deleteTips)
                        .setPositiveButton(R.string.delete) { dialog, which ->
                            viewmodel.delete(mParent !!, mScope)
                        }
            }
        }
        permisionCheck()
    }

    override fun onNavigationItemSelected(item : MenuItem) : Boolean {
        var data : Bundle? = null
        when (item.getItemId()) {
            R.id.menu_disconnet -> {
                if (P2PManager.isConnected()) {
                    mParent?.dialog {
                        it.setMessage(R.string.msg_disconnect)
                                .setPositiveButton(R.string.stopAndDisconnect) { dialog, which ->
                                    P2PManager.stopConnect(context !!)
                                    P2PManager.connected.value = null
                                    mParent?.stopService(Intent(mParent, FileService::class.java))
                                    mParent?.supportFinishAfterTransition()
                                }
                                .setNegativeButton(R.string.onlyDisconnect) { dialog, which ->
                                    mParent?.stopService(Intent(mParent, FileService::class.java))
                                }
                    }
                } else {
                    mParent?.fragment(DiscoverFragment::class, addToIt = false)
                }
            }

            R.id.menu_history -> {
                data = Bundle()
                data.putBoolean("isHistory", true)
                mParent?.fragment(HistoryFragment::class, data)
            }

            R.id.menu_connect_chat -> mParent?.fragment(ChatFragment::class, P2PManager.bundle())

            R.id.menu_payment -> mParent?.fragment(PayFragment::class)

            R.id.menu_help -> {
                data = Bundle()
                data.putInt("type", 0)
                mParent?.fragment(TextFragment::class, data)
            }

            R.id.menu_about -> {
                data = Bundle()
                data.putInt("type", 1)
                mParent?.fragment(TextFragment::class, data)
            }

            R.id.menu_setting -> startActivity(Intent(mParent, SettingActivity::class.java))
        }
        return true
    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (INTENT_CODE == requestCode) {
            permisionCheck()
        }
    }

    fun permisionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = ContextCompat.checkSelfPermission(mParent as Context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permission == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE)
            } else {
                load()
            }
        }
    }

    fun load() {
        val viewmodel = ViewModelProviders.of(mParent !!).get(FileViewModel::class.java)
        viewmodel.load(mScope, context !!)

        context?.database(mScope) { dao ->
            val datas = dao.waitRecords()
            if (datas.isNotEmpty()) {
                mParent?.runOnUiThread {
                    mParent?.dialog {
                        it.setMessage(R.string.detect_unfinished_task)
                                .setPositiveButton(R.string.send) { dialog, which ->
                                    mParent?.fragment(HistoryFragment::class, P2PManager.bundle())
                                }.setNegativeButton(R.string.later, null)

                    }
                }
            }
        }

        val intent = Intent(mParent, FileService::class.java)
        intent.putExtras(P2PManager.bundle())
        context?.startService(intent)
    }

    fun openSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", (mParent as Context).packageName, null)
        intent.data = uri
        startActivityForResult(intent, INTENT_CODE)
    }

    override fun onRequestPermissionsResult(requestCode : Int, permissions : Array<out String>, grantResults : IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PERMISSION_CODE == requestCode && ! grantResults.isEmpty()) {
            if (shouldShowRequestPermissionRationale(permissions[0])) {
                mParent?.dialog {
                    it.setMessage(R.string.viewTips)
                            .setPositiveButton(R.string.goTurnOn) { dialog, which ->
                                openSetting()
                            }
                }
            }
        }
    }
}
