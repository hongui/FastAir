package com.mob.lee.fastair.fragment

import android.widget.ImageView
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment

/**
 * Created by Andy on 2018/1/20.
 */
class PayFragment:AppFragment(){
    override val layout: Int= R.layout.fragment_payment

    override fun setting() {
        title(R.string.donate)

        view<ImageView>(R.id.payAlipay)?.setImageResource(R.drawable.alipay)
        view<ImageView>(R.id.payWechat)?.setImageResource(R.drawable.wechat)
    }

}