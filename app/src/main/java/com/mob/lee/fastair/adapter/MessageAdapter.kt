package com.mob.lee.fastair.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.model.Message

/**
 * Created by Andy on 2017/6/19.
 */
class MessageAdapter : RecyclerView.Adapter<ViewHolder>() {
    val mMessages = ArrayList<Message>()

    override fun getItemCount(): Int =mMessages.size

    override fun getItemViewType(position: Int): Int {
        val message = mMessages[position]
        when (message.type) {
            Message.OTHER -> return R.layout.item_chat_receive

            Message.SELF -> return R.layout.item_chat_send
        }
        return R.layout.item_chat_date
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent?.context).inflate(viewType, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = mMessages[position]
        holder.itemView.setOnLongClickListener { v ->
            popupMenu(v, message.content)
            true
        }
        when (message.type) {
            Message.OTHER -> holder.text(R.id.item_chat_receive_content, message.content)

            Message.SELF -> holder.text(R.id.item_chat_send_content, message.content)
        }
    }

    private fun popupMenu(view: View, content: String) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.menu_popup)
        popupMenu.setOnMenuItemClickListener { item ->
            if (R.id.menu_popup_copy == item.itemId) {
                val clip = ClipData.newPlainText("fastAir", content)
                val manager = view.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                manager.primaryClip = clip
                Toast.makeText(view.context, R.string.toast_alreadyCopy, Toast.LENGTH_SHORT).show()
                popupMenu.dismiss()
            }
            true
        }
        popupMenu.show()
    }

    fun add(msg:Message){
        mMessages.add(msg)
        notifyItemInserted(mMessages.size)
    }

    fun update(msg:Message){
        val index=mMessages.indexOf(msg)
        notifyItemChanged(index)
    }
}