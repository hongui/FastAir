package com.mob.lee.fastair.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.HomeAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.model.category
import com.mob.lee.fastair.p2p.P2PManager
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * Created by Andy on 2017/6/7.
 */
class HomeFragment : AppFragment(), NavigationView.OnNavigationItemSelectedListener {
    val PERMISSION_CODE = 12
    val INTENT_CODE = 123

    override fun layout(): Int = R.layout.fragment_home

    override fun setting() {
        homeContent?.setLayoutManager(GridLayoutManager(mParent, 3))
        homeContent?.adapter = HomeAdapter().addAll(category())

        val toolbar = view?.findViewById<Toolbar>(R.id.toolbar)
        val toggle = ActionBarDrawerToggle(mParent as Activity, homeDrawer, toolbar, R.string.toggle_open, R.string.toggle_close)
        toolbar?.title = getString(R.string.app_description)
        toggle.syncState()
        homeDrawer?.addDrawerListener(toggle)

        homeReceive?.setOnClickListener {
            mParent?.fragment(DiscoverFragment::class)
        }
        homeNavgation?.setNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        val item = homeNavgation.menu.findItem(R.id.menu_disconnet)
        if (P2PManager.connected) {
            item.title = "断开连接"
        } else {
            item.title = "连接设备"
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var data: Bundle? = null
        when (item.getItemId()) {
            R.id.menu_disconnet -> {
                if (P2PManager.connected) {
                    P2PManager.disconnect(context)
                    val item = homeNavgation.menu.findItem(R.id.menu_disconnet)
                    item.title = "连接设备"
                } else {
                    data = Bundle()
                    data.putBoolean("isJustConnect", true)
                    mParent?.fragment(DiscoverFragment::class, data)
                }
            }
            R.id.menu_history -> {
                data = Bundle()
                data.putBoolean("isHistory", true)
                mParent?.fragment(HistoryFragment::class, data)
            }

            R.id.menu_change_path -> mParent?.fragment(PathPickFragment::class)

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
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
            }
        }
    }

    fun openSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", (mParent as Context).packageName, null)
        intent.data = uri
        startActivityForResult(intent, INTENT_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PERMISSION_CODE == requestCode && !grantResults.isEmpty()) {
            if (shouldShowRequestPermissionRationale(permissions[0])) {
                showDialog(getString(R.string.viewTips),
                        { dialog, which ->
                            openSetting()
                        },
                        getString(R.string.goTurnOn))
            }
        }
    }
}
