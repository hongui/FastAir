package com.mob.lee.fastair.fragment

import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import kotlinx.android.synthetic.main.fragment_text.*


/**
 * Created by Andy on 2018/1/20.
 */
class TextFragment : AppFragment() {

    override val layout: Int = R.layout.fragment_text


    override fun setting() {
        val type = arguments?.getInt("type") ?: 0

        val name = if (0 == type) {
            title("使用帮助")
            "help.html"
        } else {
            title("关于")
            "about.html"
        }
        val url = "file:///android_asset/$name"
        webView.loadUrl(url)
    }
}