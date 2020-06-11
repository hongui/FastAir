package com.mob.lee.fastair.model

import androidx.lifecycle.MutableLiveData

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/11 10:57
 * @Description:    无
 */
class DataLoad<D> : MutableLiveData<Pair<Int, D?>>() {
    var code: Int = LOADING
    var data: D? = null
    var msg: String? = null

    fun emit(code: Int = NEXT, data: D? = null, msg: String? = null) {
        this.value = code to data
        this.code = code
        this.msg = msg
    }

    fun next(data: D?) {
        if (code != NEXT) {
            start()
        }
        emit(NEXT, data = data)
    }

    fun error(msg: String?, code: Int = ERROR) {
        emit(code, msg = msg)
    }

    fun loading() {
        emit(LOADING)
    }

    fun start() {
        emit(STARTED)
    }

    fun empty() {
        emit(EMPTY)
    }

    fun complete() {
        emit(COMPLETE)
    }

    companion object {
        const val LOADING = 1
        const val STARTED = 2
        const val NEXT = 3
        const val EMPTY = 4
        const val COMPLETE = 5
        const val ERROR = 6
    }
}