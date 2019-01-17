package com.mob.lee.fastair.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

/**
 * Created by Andy on 2017/6/1.
 */
open class AppActivity : AppCompatActivity() {

    val mScope:AndroidScope by lazy {
        AndroidScope()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScope.create()
    }

    override fun onDestroy() {
        super.onDestroy()
        mScope.destory()
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

    fun fragment(cls: KClass<*>, bundle: Bundle = Bundle(), content:Int=android.R.id.content,addToIt: Boolean = true) {
        val manager = supportFragmentManager
        var fragment=manager.findFragmentByTag(cls.simpleName)
        val alreadyAdd=null!=fragment
        if(!alreadyAdd) {
            fragment=Fragment.instantiate(this,cls.qualifiedName)
        }
        fragment?:throw NullPointerException("fragment is null")

        if (null != fragment.arguments) {
            fragment.arguments?.putAll(bundle)
        } else {
            fragment.arguments = bundle
        }
        val transaction = manager.beginTransaction()

        transaction.replace(content, fragment,cls.simpleName)
        if (addToIt && !alreadyAdd) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}