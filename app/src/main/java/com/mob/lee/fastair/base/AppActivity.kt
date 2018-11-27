package com.mob.lee.fastair.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlin.reflect.KClass

/**
 * Created by Andy on 2017/6/1.
 */
open class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        if (handle()) {
            return
        }
        val count = supportFragmentManager.backStackEntryCount
        if (1 < count) {
            super.onBackPressed()
        } else {
            supportFinishAfterTransition()
        }
    }

    fun handle(): Boolean {
        val fragments = supportFragmentManager.fragments
        if (null == fragments || fragments.isEmpty()) {
            return false
        }
        for (fragment in fragments) {
            if (null == fragment) {
                return false
            }
            val hanlded = fragment is OnBackpressEvent && fragment.onPressed()
            if (hanlded) {
                return true
            }
        }
        return false
    }

    fun fragment(cls: KClass<out AppFragment>, bundle: Bundle = Bundle(), addToIt: Boolean = true) {
        val manager = supportFragmentManager
        var fragment=manager.findFragmentByTag(cls.simpleName)
        if(null==fragment) {
            val constructors = cls.constructors
            val constructor = constructors.elementAt(0)
            fragment = constructor.call()
        }
        if (null != fragment.arguments) {
            fragment.arguments?.putAll(bundle)
        } else {
            fragment.arguments = bundle
        }
        val transaction = manager.beginTransaction()
        transaction.replace(android.R.id.content, fragment,cls.simpleName)

        if (addToIt) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}