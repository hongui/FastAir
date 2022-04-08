package com.mob.lee.fastair.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import com.mob.lee.fastair.ContainerActivity
import com.mob.lee.fastair.R
import com.mob.lee.fastair.viewmodel.AppViewModel

/**
 * Created by Andy on 2017/6/1.
 */

abstract class AppFragment : Fragment() {
    protected abstract val layout: Int
    protected open val defaultContainer: Int = R.layout.container
    var mParent: ContainerActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mParent = context as ContainerActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (-1 == defaultContainer) {
            inflater.inflate(layout, container, false)
        } else {
            val root = inflater.inflate(defaultContainer, container, false)
            val flContent = root?.findViewById<FrameLayout>(R.id.flContainer)
            inflater.inflate(layout, flContent, true)
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    open fun setting() {}

    fun title(titleId: Int, canReturn: Boolean = true) {
        title(getString(titleId), canReturn)
    }

    fun title(title: CharSequence, canReturn: Boolean = true) {
        val toolbar = view?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.setTitle(title)
        val appActivity = activity as AppCompatActivity
        appActivity.setSupportActionBar(toolbar)
        if (canReturn) {
            appActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar?.setNavigationOnClickListener { mParent?.onBackPressed() }
        }
    }

    fun <D> LiveData<D>.observe( action: (D) -> Unit) {
        this.observe(this@AppFragment, Observer { action(it) })
    }

    fun navigation(
        id: Int,
        args: (Bundle.() -> Unit)? = null,
        options: (NavOptions.Builder.() -> Unit)? = null
    ) {
        val bundle = args?.let {
            val bundle = Bundle()
            it(bundle)
            bundle
        }
        val opt = options?.let {
            val builder = NavOptions.Builder()
            options(builder)
            builder.build()
        }
        mParent?.mNavController?.navigate(id, bundle, opt)
    }

    fun navigationBack(
        @IdRes destinationId: Int? = mParent?.mNavController?.currentDestination?.id,
        inclusive: Boolean = false
    ) {
        mParent?.mNavController?.popBackStack(destinationId ?: -1, inclusive)
    }

    inline fun <reified D : AppViewModel> viewModel(): D {
        val viewModel = ViewModelProvider(this).get(D::class.java)
        return viewModel
    }

    inline fun <reified D : AppViewModel> activityViewModel(): D {
        val viewModel = ViewModelProvider(mParent!!).get(D::class.java)
        return viewModel
    }
}