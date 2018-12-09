package com.mob.lee.fastair.model

import android.provider.MediaStore
import com.mob.lee.fastair.R

class ZipCategory(override val title : Int= R.string.category_zip) :Category(){
    override fun select()= "${MediaStore.MediaColumns.DATA} LIKE ? OR ${MediaStore.MediaColumns.DATA} LIKE ? OR ${MediaStore.MediaColumns.DATA} LIKE ?"

    override fun value() =arrayOf("%.zip", "%.rar", "%.7z")
}