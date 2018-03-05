package com.mob.lee.fastair.service

import com.mob.lee.fastair.model.Record

/**
 * Created by Andy on 2017/9/18.
 */
interface FileChangeListener{

    fun onStart(record: Record)

    fun onProgress(record: Record, progress:Int)

    fun onComplete(record: Record, state:Int)
}