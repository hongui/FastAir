package com.mob.lee.fastair.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.wifi.p2p.WifiP2pDevice
import android.os.IBinder
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.MessageAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.base.OnBackpressEvent
import com.mob.lee.fastair.model.Message
import com.mob.lee.fastair.p2p.P2PManager
import com.mob.lee.fastair.p2p.Subscriber
import com.mob.lee.fastair.service.BinderImpl
import com.mob.lee.fastair.service.MessageService
import com.mob.lee.fastair.utils.errorToast
import kotlinx.android.synthetic.main.fragment_chat.*

/**
 * Created by Andy on 2017/6/7.
 */
class ChatFragment : AppFragment(), OnBackpressEvent, Subscriber {
    var mBack = false
    var mConnect : ServiceConnection? = null
    var mService : MessageService? = null
    lateinit var mAdapter : MessageAdapter

    override fun wifiState(enable : Boolean) {
    }

    override fun peers(devices : List<WifiP2pDevice>) {
    }

    override fun connect(connected : Boolean) {
        showDialog(R.string.msg_disconnect,
                { dialog, which ->
                    mParent?.fragment(DiscoverFragment::class, addToIt = false)
                }, R.string.reconnect,
                R.string.title_error,
                R.string.exit, { dialog, which ->
            mParent?.supportFinishAfterTransition()
        })
    }


    override fun layout() : Int = R.layout.fragment_chat

    override fun setting() {
        toolbar(R.string.base_chat)
        P2PManager.addSubcriber(this)

        mAdapter = MessageAdapter()
        chatContent?.layoutManager = LinearLayoutManager(mParent as Context)
        chatContent?.adapter = mAdapter

        chatInput?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s : Editable?) {
            }

            override fun beforeTextChanged(s : CharSequence?, start : Int, count : Int, after : Int) {
            }

            override fun onTextChanged(s : CharSequence?, start : Int, before : Int, count : Int) {
                if (TextUtils.isEmpty(s)) {
                    chatSend?.isEnabled = false
                } else {
                    chatSend?.isEnabled = true
                }
            }
        })

        chatSend?.setOnClickListener {
            val s = chatInput?.text.toString()
            chatInput?.setText("")
            if (s.matches("\\s+".toRegex())) {
                toast(R.string.toast_sendIllegal)
                return@setOnClickListener
            }
            val message = Message(s)
            mAdapter.add(message)
            chatContent?.smoothScrollToPosition(mAdapter.getItemCount())
            mService?.write(s)
        }

        val intent = Intent(context, MessageService::class.java)
        intent.putExtras(arguments)
        mConnect = object : ServiceConnection {
            override fun onServiceDisconnected(name : ComponentName?) {
                context?.errorToast("连接断开")
            }

            override fun onServiceConnected(name : ComponentName?, service : IBinder?) {
                if (null == service) {
                    return
                }
                val binder = service as BinderImpl
                mService = binder.mService as MessageService
                mService?.mMessageListener = {
                    val msg = it.obj as? String
                    msg?.let {
                        mAdapter.add(Message(it, Message.OTHER))
                        chatContent?.smoothScrollToPosition(mAdapter.getItemCount())
                    }
                }
            }
        }
        context?.bindService(intent, mConnect, Context.BIND_AUTO_CREATE)
        context?.startService(intent)
    }

    override fun onPressed() : Boolean {
        if (mBack) {
            return false
        }
        showDialog(getString(R.string.chatOverInfo), { dialog, which ->
            mBack = true
            mParent?.onBackPressed()
        }, getString(R.string.disconnect))
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        P2PManager.removeSubcripber(this)
        mParent?.stopService(Intent(context, MessageService::class.java))
        if (null != mConnect) {
            context?.unbindService(mConnect)
        }
    }
}