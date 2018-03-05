package com.mob.lee.fastair.service

import android.app.Service
import android.os.Binder

/**
 * Created by Andy on 2017/9/18.
 */
class BinderImpl(val mService:Service):Binder()