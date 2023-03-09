package com.mob.lee.fastair.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.adapter.MessageAdapter
import com.mob.lee.fastair.io.state.MessageState
import com.mob.lee.fastair.model.Message
import com.mob.lee.fastair.service.BinderImpl
import com.mob.lee.fastair.service.MessageService
import com.mob.lee.fastair.utils.errorToast

/**
 * Created by Andy on 2017/6/7.
 */
class ChatFragment : ConnectFragment() {
    var mConnect: ServiceConnection? = null
    var mService: MessageService? = null
    lateinit var mAdapter: MessageAdapter
    override val layout: Int = R.layout.fragment_chat

    override fun setting() {
        title(R.string.chat)

        mAdapter = MessageAdapter()
        val chatContent = view<RecyclerView>(R.id.chatContent)
        val chatInput = view<EditText>(R.id.chatInput)
        val chatSend = view<TextView>(R.id.chatSend)
        chatContent?.layoutManager = LinearLayoutManager(mParent)
        chatContent?.adapter = mAdapter

        chatInput?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                chatSend?.isEnabled = !TextUtils.isEmpty(s)
            }
        })

        chatSend?.setOnClickListener {
            val s = chatInput?.text.toString()
            chatInput?.setText("")
            if (s.matches("\\s+".toRegex())) {
                mParent?.errorToast(R.string.send_Illegal)
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
            override fun onServiceDisconnected(name: ComponentName?) {
                context?.errorToast("连接断开")
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                if (null == service) {
                    return
                }
                val binder = service as BinderImpl
                mService = binder.mService as MessageService
                mService?.mMessageListener = {state->
                    if(state is MessageState){
                        mAdapter.add(Message(state.msg, Message.OTHER))
                        chatContent?.smoothScrollToPosition(mAdapter.getItemCount())
                    }
                }
            }
        }
        context?.startService(intent)
        context?.bindService(intent, mConnect!!, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        mParent?.stopService(Intent(context, MessageService::class.java))
        mConnect?.let {
            context?.unbindService(it)
        }
    }
}