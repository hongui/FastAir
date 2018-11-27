package com.mob.lee.fastair.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class AndroidScope:CoroutineScope {
    lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = job+Dispatchers.Main

    fun create(){
        job=Job()
    }

    fun destory(){
        job.cancel()
    }
}