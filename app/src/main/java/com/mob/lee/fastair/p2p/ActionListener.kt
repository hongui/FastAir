package com.mob.lee.fastair.p2p

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AlertDialog
import com.mob.lee.fastair.ContainerActivity
import com.mob.lee.fastair.R
import com.mob.lee.fastair.utils.dialog
import com.mob.lee.fastair.utils.errorToast

/**
 * Created by Andy on 2017/8/17.
 */
class ActionListener(val context : Context?) : WifiP2pManager.ActionListener {
    override fun onSuccess() {

    }

    override fun onFailure(reason : Int) {
        when (reason) {

            WifiP2pManager.P2P_UNSUPPORTED -> {
                context?.dialog {
                            setTitle(R.string.wramTips)
                            .setMessage(R.string.tip_error_p2p_unsupport)
                            .setPositiveButton(R.string.knowIt) { _, _ ->
                                (context as? ContainerActivity)?.onBackPressed()
                            }
                } ?: let {
                    context?.errorToast(R.string.tip_error_p2p_unsupport)
                }
            }

            WifiP2pManager.BUSY -> {
                context?.let {
                    it.errorToast(R.string.tip_error_p2p_busy)
                    P2PManager.stopConnect(it)
                }
            }

            WifiP2pManager.ERROR -> context?.let {
                it.errorToast(R.string.tip_error_p2p_inter)
                P2PManager.stopConnect(it)
            }
        }
    }
}