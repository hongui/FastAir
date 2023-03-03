package com.mob.lee.fastair.io

/**
 * Created by Andy on 2018/1/16.
 */
enum class ProtocolType(val type:Byte){
    /*字节*/
    B(ProtocolType.CB),
    /*短整型*/
    S(ProtocolType.CS),
    /*整形*/
    I(ProtocolType.CI),
    /*长整形*/
    L(ProtocolType.CL),
    /*浮点*/
    F(ProtocolType.CF),
    /*高精度*/
    D(ProtocolType.CD),
    /*字符串*/
    W(ProtocolType.CW),
    /*分割符用*/
    E(ProtocolType.CE);

    companion object {
        @JvmStatic val CB:Byte=0x01
        @JvmStatic val CS:Byte= (0x01 shl 1).toByte()
        const val CS:Byte=0x01 shl 1
        const val CI:Byte=0x01 shl 2
        const val CL:Byte=0x01 shl 3
        const val CF:Byte=0x01 shl 4
        const val CD:Byte=0x01 shl 5
        const val CW:Byte=0x01 shl 6
        const val CE:Byte=0x0

        fun wrap(value:Byte):ProtocolType=when(value){
            CB->B
            CS->S
            CI->I
            CL->L
            CF->F
            CD->D
            CW->W
            else->E
        }
    }
}