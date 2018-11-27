package com.mob.lee.fastair.fragment

import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.imageloader.ImageLoader
import com.mob.lee.fastair.utils.display
import kotlinx.android.synthetic.main.fragment_payment.*

/**
 * Created by Andy on 2018/1/20.
 */
class PayFragment:AppFragment(){
    override fun layout()= R.layout.fragment_payment

    override fun setting() {
        toolbar("谢谢客官打赏")

        display(context!!,R.drawable.alipay,payAlipay)
        display(context!!,R.drawable.wechat,payWechat)
    }

}