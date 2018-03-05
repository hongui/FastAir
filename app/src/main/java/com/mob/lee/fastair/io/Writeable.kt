package com.mob.lee.fastair.io

import java.io.OutputStream

/**
 * Created by Andy on 2017/12/23.
 */
interface Writeable:Progress{
    fun stream():OutputStream
}