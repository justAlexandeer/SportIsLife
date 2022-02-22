package com.myprog.sportislife.ui.view

import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.myprog.sportislife.R

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey)

        val timePreferences: EditTextPreference? = findPreference("preference_max_value_time")
        val caloriesPreferences: EditTextPreference? = findPreference("preference_max_value_calories")
        val stepsPreferences: EditTextPreference? = findPreference("preference_max_value_steps")

        timePreferences?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
        }
        caloriesPreferences?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
        }
        stepsPreferences?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }


}