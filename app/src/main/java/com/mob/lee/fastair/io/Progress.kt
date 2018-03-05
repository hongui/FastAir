package com.mob.lee.fastair.io

/**
 * Created by Andy on 2017/12/28.
 */
interface Progress{

    fun progress(progress:Long)

    fun result(isOk:Boolean)
}