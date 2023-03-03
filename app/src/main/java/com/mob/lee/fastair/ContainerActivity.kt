package com.mob.lee.fastair

import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.mob.lee.fastair.p2p.failedReason
import com.mob.lee.fastair.utils.errorToast
import com.mob.lee.fastair.viewmodel.DeviceViewModel
import com.mob.lee.fastair.viewmodel.HomeViewModel

/**
 * Created by Andy on 2017/6/2.
 */
class ContainerActivity : AppCompatActivity() {

    val mNavController by lazy {
        findNavController(R.id.containerFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        handleIntent(intent)

        val viewModel: DeviceViewModel by viewModels()
        viewModel.run {
            init(this@ContainerActivity)
            discover(object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                }

                override fun onFailure(reason: Int) {
                    errorToast(failedReason(reason))
                }
            })
            connectState.observe(this@ContainerActivity) {
                if (!it) {
                    errorToast(R.string.disconnected)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    fun handleIntent(intent: Intent?) {
        val data = intent?.clipData
        if (null != data) {
            val v: HomeViewModel by viewModels()
            v.parseClipData(this, data).observe(this) {
                mNavController.navigate(R.id.beforeFragment)
            }
        }
    }
}