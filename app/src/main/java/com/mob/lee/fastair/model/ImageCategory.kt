package com.mob.lee.fastair.model

import android.provider.MediaStore
import com.mob.lee.fastair.R

class ImageCategory(override val title : Int= R.string.category_picture) :Category(){

    override fun uri() = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
}