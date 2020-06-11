package com.mob.lee.fastair.model

data class DataWrap<D>(var code:Int= SUCCESS, var data:D?=null, var msg:String?=null){

    fun isSuccess()= SUCCESS==code

    companion object{
        const val SUCCESS=0
        const val ERROR=1
        fun <D> success(data:D?)=DataWrap(SUCCESS, data)
        fun <D> error(msg: String?=null,code: Int= ERROR)=DataWrap<D>(ERROR, msg=msg)
    }
}