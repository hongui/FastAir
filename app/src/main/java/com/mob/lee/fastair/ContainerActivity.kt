package com.mob.lee.fastair

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.mob.lee.fastair.p2p.P2PManager
import com.mob.lee.fastair.utils.errorToast
import com.mob.lee.fastair.viewmodel.DeviceViewModel

/**
 * Created by Andy on 2017/6/2.
 */
class ContainerActivity : AppCompatActivity() {

    val mNavController by lazy {
        findNavController(R.id.hostFragment)
    }
    val viewModel by lazy {
        ViewModelProvider(this).get(DeviceViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        P2PManager.connectLiveData.observe(this, Observer {
            if (false == it) {
                errorToast(R.string.msg_disconnect)
                mNavController.popBackStack(R.id.homeFragment, true)
            }
        })

        P2PManager.p2pInfoLiveData.observe(this, Observer {
            it?.let {
                viewModel.saveInfo(this, it)
            }
        })
    }
}