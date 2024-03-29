package com.mob.lee.fastair.fragment

import android.os.Bundle
import android.os.Environment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.mob.lee.fastair.ContainerActivity
import com.mob.lee.fastair.R
import com.mob.lee.fastair.utils.errorToast

/**
 *
 * @Author:         Andy（632518410）
 * @CreateDate:     2020/6/18 15:59
 * @Description:    无
 */
class SettingContentFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_setting)

        val key = getString(R.string.key_default_download)
        val preferenceDownload = findPreference<Preference>(key)
        preferenceDownload?.setOnPreferenceClickListener {
            val activity = context as? ContainerActivity
            activity?.mNavController?.navigate(R.id.pathPickFragment)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        updateSummary()
    }

    fun updateSummary() {
        val key = getString(R.string.key_default_download)
        val preferenceDownload = findPreference<Preference>(key)
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val defaultDownloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val manager = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val value = manager.getString(key, defaultDownloadPath?.absolutePath)
            preferenceDownload?.summary = value
        } else {
            requireContext().errorToast(R.string.storage_not_ready)
            preferenceDownload?.isEnabled = false
        }
    }
}