package com.mob.lee.fastair.model

import android.os.Build
import android.provider.MediaStore
import com.mob.lee.fastair.R

class ImageCategory(override val title: Int = R.string.category_picture) : Category() {

    override fun uri() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
}