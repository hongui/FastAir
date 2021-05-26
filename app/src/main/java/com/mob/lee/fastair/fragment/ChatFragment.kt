package com.mob.lee.fastair.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.MessageAdapter
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.model.Message
import com.mob.lee.fastair.service.BinderImpl
import com.mob.lee.fastair.service.MessageService
import com.mob.lee.fastair.utils.errorToast
import kotlinx.android.synthetic.main.fragment_chat.*

/**
 * Created by Andy on 2017/6/7.
 */
class ChatFragment : ConnectFragment() {
    var mBack = false
    var mConnect : ServiceConnection? = null
    var mService : MessageService? = null
    lateinit var mAdapter : MessageAdapter
    override val layout: Int = R.layout.fragment_chat

    override fun setting() {
        title(R.string.base_chat)

        mAdapter = MessageAdapter()
        chatContent?.layoutManager = LinearLayoutManager(mParent)
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
                mParent?.errorToast(R.string.toast_sendIllegal)
                return@setOnClickListener
            }
            val message = Message(s)
            mAdapter.add(message)
            chatContent?.smoothScrollToPosition(mAdapter.getItemCount())
            mService?.write(s)
        }

        val intent = Intent(context, MessageService::class.java)
        arguments?.let {
            intent.putExtras(it)
        }
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
        context?.bindService(intent, mConnect!!, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        mConnect?.let {
            context?.unbindService(it)
        }
    }
}