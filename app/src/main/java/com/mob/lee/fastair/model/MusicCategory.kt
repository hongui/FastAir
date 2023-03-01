package com.mob.lee.fastair.model

import android.os.Build
import android.provider.MediaStore
import com.mob.lee.fastair.R

class MusicCategory(override val title: Int = R.string.category_music) : Category() {

    override fun uri() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL
        )
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }


}