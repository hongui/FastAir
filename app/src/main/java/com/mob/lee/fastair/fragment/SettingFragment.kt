package com.mob.lee.fastair.fragment

import android.os.Bundle
import android.os.Environment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.mob.lee.fastair.R
import com.mob.lee.fastair.base.AppActivity
import com.mob.lee.fastair.utils.errorToast


class SettingFragment: PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState : Bundle?, rootKey : String?) {
        addPreferencesFromResource(R.xml.pref_setting)

        val key=getString(R.string.key_default_download)
        val preferenceDownload=findPreference(key)
        preferenceDownload?.setOnPreferenceClickListener {
            val activity=context as? AppActivity
            activity?.fragment(PathPickFragment::class,content = R.id.setting_fragment)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        updateSummary()
    }

    fun updateSummary(){
        val key=getString(R.string.key_default_download)
        val preferenceDownload=findPreference(key)
        if(Environment.MEDIA_MOUNTED==Environment.getExternalStorageState()){
            val defaultDownloadPath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val manager=PreferenceManager.getDefaultSharedPreferences(context)
            val value=manager.getString(key,defaultDownloadPath?.absolutePath)
            preferenceDownload?.summary=value
        }else{
            context?.errorToast(R.string.storage_not_ready)
            preferenceDownload.isEnabled=false
        }
    }
}