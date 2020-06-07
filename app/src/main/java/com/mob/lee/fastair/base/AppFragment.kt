package com.mob.lee.fastair.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mob.lee.fastair.ContainerActivity
import com.mob.lee.fastair.R
import com.mob.lee.fastair.model.StatusError
import com.mob.lee.fastair.model.StatusLoading
import com.mob.lee.fastair.model.StatusSuccess
import com.mob.lee.fastair.viewmodel.AppViewModel

/**
 * Created by Andy on 2017/6/1.
 */

abstract class AppFragment : Fragment() {
    protected abstract val layout: Int
    var mParent: ContainerActivity? = null
    val mViewSwitcher by lazy {
        view?.findViewById<ViewSwitcher>(R.id.vsRoot)
    }
    val mViewSwitcherContent by lazy {
        view?.findViewById<ViewSwitcher>(R.id.vsContent)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mParent = context as ContainerActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.container, container, false)
        val flContent = root?.findViewById<FrameLayout>(R.id.flContainer)
        inflater.inflate(layout, flContent, true)
        return root
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

    fun startLoading(msg: CharSequence? = getString(R.string.loading)) {
        if (R.id.clLoading != mViewSwitcher?.currentView?.id) {
            mViewSwitcher?.showNext()
        }
        mViewSwitcher?.findViewById<TextView>(R.id.tvLoading)?.setText(msg)
    }


    fun error(msg: CharSequence? = getString(R.string.tips_error), action: (() -> Unit)? = null) {
        stopLoading()
        if (R.id.clError != mViewSwitcherContent?.currentView?.id) {
            mViewSwitcher?.showNext()
        }
        mViewSwitcherContent?.findViewById<TextView>(R.id.tvErrorMsg)?.text = msg
        mViewSwitcherContent?.findViewById<TextView>(R.id.btnErrorRetry)?.setOnClickListener {
            action?.invoke()
        }
    }

    fun stopLoading() {
        if (R.id.vsContent != mViewSwitcher?.currentView?.id) {
            mViewSwitcher?.showNext()
        }
    }

    fun content() {
        stopLoading()
        if (R.id.flContainer != mViewSwitcherContent?.currentView?.id) {
            mViewSwitcher?.showNext()
        }
    }

    fun <D> observe(liveData: LiveData<D>, action: (D) -> Unit) {
        liveData.observe(this, Observer { action(it) })
    }

    fun navigation(id: Int, args: (Bundle.() -> Unit)? = null) {
        mParent?.mNavController?.navigate(id, args?.let {
            val bundle = Bundle()
            it(bundle)
            bundle
        })
    }

    inline fun <reified D : AppViewModel> viewModel(): D {
        val viewModel = ViewModelProviders.of(this).get(D::class.java)
        observe(viewModel.stateLiveData) {
            when (it) {
                is StatusLoading -> startLoading(it.msg)
                is StatusSuccess -> content()
                is StatusError -> error(it.msg)
            }
        }
        return viewModel
    }
}