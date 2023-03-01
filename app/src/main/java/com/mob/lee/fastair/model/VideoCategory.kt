package com.mob.lee.fastair.model

import android.os.Build
import android.provider.MediaStore
import com.mob.lee.fastair.R

class VideoCategory(override val title: Int = R.string.category_video) : Category() {

    override fun uri() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        )
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }
}