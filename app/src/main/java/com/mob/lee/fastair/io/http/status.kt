package com.mob.lee.fastair.io.http

const val SUCCESS = 200
const val NOTFOUNT = 404
const val SERVERERROR = 500

fun status(code: Int) = when (code) {
    SUCCESS -> "OK"
    NOTFOUNT -> "Not Found"
    else -> "Internal Server Error"
}