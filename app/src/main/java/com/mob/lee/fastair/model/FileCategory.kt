package com.mob.lee.fastair.model

import com.mob.lee.fastair.R

/**
 * Created by Andy on 2017/6/7.
 */
data class FileCategory(val category: Int) {
    var icon: Int = 0
    var title: Int = 0

    init {
        when (category) {
            PICTURE -> {
                icon = R.drawable.ic_category_picture
                title = R.string.category_picture
            }

            VIDEO -> {
                icon=R.drawable.ic_category_video
                title=R.string.category_video
            }

            MUSIC -> {
                icon=R.drawable.ic_category_music
                title=R.string.category_music
            }

            WORD -> {
                icon=R.drawable.ic_category_word
                title=R.string.category_word
            }

            EXCEL -> {
                icon=R.drawable.ic_category_excel
                title=R.string.category_excel
            }

            POWERPOINT -> {
                icon=R.drawable.ic_category_powerpoint
                title=R.string.category_powerpoint
            }

            PDF -> {
                icon=R.drawable.ic_category_pdf
                title=R.string.category_pdf
            }

            TXT -> {
                icon=R.drawable.ic_category_txt
                title=R.string.category_txt
            }

            ZIP -> {
                icon=R.drawable.ic_category_zip
                title=R.string.category_zip
            }

            APPLICATION -> {
                icon=R.drawable.ic_category_apk
                title=R.string.category_application
            }

            OTHER -> {
                icon=R.drawable.ic_category_other
                title=R.string.category_other
            }

            CHAT -> {
                icon=R.drawable.ic_category_chat
                title=R.string.base_chat
            }
        }
    }
}