package com.mob.lee.fastair.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Andy on 2017/6/2.
 */
data class Message(var content: String = "",
                   var from: Int = SELF,
                   var type: Int = MESSAGE_TEXT,
                   var contentLength: Int = content.length,
                   var date: Long = System.currentTimeMillis()) : Parcelable {
    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(content)
        dest?.writeInt(from)
        dest?.writeInt(type)
        dest?.writeInt(contentLength)
        dest?.writeLong(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object{
        val SELF=1
        val OTHER=2
        @JvmField val CREATOR: Parcelable.Creator<Message> = object : Parcelable.Creator<Message> {
            override fun createFromParcel(source: Parcel): Message = Message(source)
            override fun newArray(size: Int): Array<Message?> = arrayOfNulls(size)
        }
    }

    constructor(source:Parcel):this(){
        content=source.readString()
        from=source.readInt()
        type=source.readInt()
        contentLength=source.readInt()
        date=source.readLong()
    }
}