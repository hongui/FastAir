package com.mob.lee.fastair.model

import android.provider.MediaStore
import com.mob.lee.fastair.R

class TextCategory(override val title : Int= R.string.category_txt) :Category(){
    override fun select() = "${MediaStore.MediaColumns.DATA} LIKE ?"

    override fun value() = arrayOf("%.txt")
}