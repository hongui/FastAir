package com.mob.lee.fastair.model

import android.provider.MediaStore
import com.mob.lee.fastair.R

class MusicCategory(override val title : Int= R.string.category_music) :Category(){

    override fun uri() = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI


}