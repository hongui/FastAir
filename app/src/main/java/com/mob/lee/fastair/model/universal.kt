package com.mob.lee.fastair.model

import android.content.Context
import android.text.format.Formatter
import java.text.SimpleDateFormat

/**
 * Created by Andy on 2017/6/2.
 */

val MESSAGE_TEXT = 1

val PICTURE = 1
val VIDEO = 2
val MUSIC = 3
val WORD = 4
val EXCEL = 5
val POWERPOINT = 6
val PDF = 7
val TXT = 8
val ZIP = 9
val APPLICATION = 10
val OTHER = 11
val CHAT = 12

val HOST = "host"
val ADDRESS = "address"
val IS_HOST = "isHost"
val IS_CHAT = "isChat"
val IS_SEND = "isSend"
val PORT="port"
val MESSAGE = "message"
val FILE = "file"
const val FILES="files"
const val COUNT="count"
val HOST_TO = "hostTO"
val CONNECTION_PORT="connectionPort"
val PORT_MESSAGE=65514
val PORT_FILE=65515
const val TEXT=1
const val BINNARY= 2

fun Long.formatDate(parttern:String="MM/dd/yyyy HH:mm"):String{
    val formater=SimpleDateFormat.getDateInstance() as SimpleDateFormat
    formater.applyPattern(parttern)
    return formater.format(this)
}

fun Long.formatSize(context:Context):String{
    return Formatter.formatFileSize(context,this)
}