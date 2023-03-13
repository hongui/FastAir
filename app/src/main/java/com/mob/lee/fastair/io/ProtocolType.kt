package com.mob.lee.fastair.io

/**
 * Created by Andy on 2018/1/16.
 */
class ProtocolType {

    companion object {
        /*字节*/
        @JvmStatic
        val B: Byte = 0x01

        /*短整型*/
        @JvmStatic
        val S: Byte = (0x01 shl 1).toByte()

        /*整形*/
        @JvmStatic
        val I: Byte = (0x01 shl 2).toByte()

        /*长整形*/
        @JvmStatic
        val L: Byte = (0x01 shl 3).toByte()

        /*浮点*/
        @JvmStatic
        val F: Byte = (0x01 shl 4).toByte()

        /*高精度*/
        @JvmStatic
        val D: Byte = (0x01 shl 5).toByte()

        /*字符串*/
        @JvmStatic
        val W: Byte = (0x01 shl 6).toByte()

        /*分割符用*/
        @JvmStatic
        val E: Byte = 0x0
    }
}