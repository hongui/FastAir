package com.mob.lee.fastair

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.mob.lee.fastair.p2p.P2PManager
import com.mob.lee.fastair.service.FileService
import com.mob.lee.fastair.utils.errorToast
import com.mob.lee.fastair.viewmodel.DeviceViewModel
import com.mob.lee.fastair.viewmodel.HomeViewModel

/**
 * Created by Andy on 2017/6/2.
 */
class ContainerActivity : AppCompatActivity() {

    val mNavController by lazy {
        findNavController(R.id.hostFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        handleIntent(intent)

        if(null==savedInstanceState) {
            P2PManager.connectLiveData.observe(this, Observer {
                if (false == it) {
                    errorToast(R.string.msg_disconnect)
                    mNavController.popBackStack(R.id.homeFragment, true)

                    try {
                        val intent = Intent(this, FileService::class.java)
                        stopService(intent)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }

                }
            })

            P2PManager.p2pInfoLiveData.observe(this, Observer {
                it?.let {
                    val viewModel:DeviceViewModel  by viewModels()
                    viewModel.saveInfo(this, it)
                }
            })
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    fun handleIntent(intent: Intent?){
        val data = intent?.clipData
        if (null != data) {
            val v:HomeViewModel  by viewModels()
            v.parseClipData(this,data).observe(this){
                mNavController.navigate(R.id.beforeFragment)
            }
        }
    }
}