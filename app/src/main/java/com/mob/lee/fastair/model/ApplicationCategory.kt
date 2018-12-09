package com.mob.lee.fastair.model

import android.provider.MediaStore
import com.mob.lee.fastair.R

class ApplicationCategory(override val title : Int= R.string.category_application) :Category(){
    override fun select() = "${MediaStore.MediaColumns.DATA} LIKE ?"

    override fun value() = arrayOf("%.apk")
}