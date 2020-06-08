package com.mob.lee.fastair.model

sealed class Status()
class StatusLoading(val msg:CharSequence?=null):Status()
class StatusSuccess():Status()
class StatusComplete():Status()
class StatusError(val msg:CharSequence?=null):Status()
class StatusEmpty():Status()