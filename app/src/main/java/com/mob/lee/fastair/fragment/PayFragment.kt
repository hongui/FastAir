package com.mob.lee.fastair.fragment

import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppFragment
import com.mob.lee.fastair.imageloader.Decoder
import kotlinx.android.synthetic.main.fragment_payment.*

/**
 * Created by Andy on 2018/1/20.
 */
class PayFragment:AppFragment(){
    override val layout: Int= R.layout.fragment_payment

    override fun setting() {
        title("谢谢客官打赏")

        //display(requireContext(),R.drawable.alipay,payAlipay)
        //display(requireContext(),R.drawable.wechat,payWechat)
    }

}