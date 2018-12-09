package com.mob.lee.fastair.model

import android.net.Uri
import android.provider.MediaStore
import com.mob.lee.fastair.R

class VideoCategory(override val title : Int= R.string.category_video) :Category(){

    override fun uri()= MediaStore.Video.Media.EXTERNAL_CONTENT_URI
}