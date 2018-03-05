package com.mob.lee.fastair.io

/**
 * Created by Andy on 2018/1/16.
 */
enum class ProtocolType(val type:Byte){
    B('B'.toByte()),
    S('S'.toByte()),
    L('L'.toByte()),
    N('N'.toByte());

    companion object {
        fun isValid(type:Byte):Boolean{
            return when(type){
                B.type,S.type,L.type,N.type->true
                else->false
            }
        }
    }
}