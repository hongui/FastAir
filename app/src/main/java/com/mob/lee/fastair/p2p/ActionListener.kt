package com.mob.lee.fastair.p2p

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import com.mob.lee.fastair.ContainerActivity
import com.mob.lee.fastair.R
import com.mob.lee.fastair.utils.dialog
import com.mob.lee.fastair.utils.errorToast

/**
 * Created by Andy on 2017/8/17.
 */
class ActionListener(val context: Context?) : WifiP2pManager.ActionListener {
    override fun onSuccess() {
    }

    override fun onFailure(reason: Int) {
        when (reason) {
            WifiP2pManager.P2P_UNSUPPORTED -> {
                context?.dialog {
                    setMessage(R.string.p2p_unsupport_error)
                            .setPositiveButton(R.string.knowIt) { _, _ ->
                                (context as? ContainerActivity)?.onBackPressed()
                            }
                } ?: let {
                    context?.errorToast(R.string.p2p_unsupport_error)
                }
            }

            WifiP2pManager.BUSY -> {
                context?.let {
                    it.errorToast(R.string.p2p_busy_error)
                    P2PManager.stopConnect()
                }
            }

            WifiP2pManager.ERROR -> context?.let {
                it.errorToast(R.string.p2p_inner_error)
                P2PManager.stopConnect()
            }
        }
    }
}