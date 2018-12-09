package com.mob.lee.fastair.model

import android.provider.MediaStore
import com.mob.lee.fastair.R

class PowerPointCategory(override val title : Int= R.string.category_powerpoint) :Category(){

    override fun select()= "${MediaStore.MediaColumns.DATA} LIKE ? OR ${MediaStore.MediaColumns.DATA} LIKE ?"

    override fun value() =arrayOf("%.ppt", "%.pptx")
}