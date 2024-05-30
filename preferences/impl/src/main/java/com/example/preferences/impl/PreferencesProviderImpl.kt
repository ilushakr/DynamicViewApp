package com.example.preferences.impl

import android.content.SharedPreferences
import com.example.preferences.api.PreferencesProvider

internal class PreferencesProviderImpl(private val prefs: SharedPreferences) : PreferencesProvider {

    override fun setValue(key: String, value: Any?) {
        with(prefs.edit()) {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                is String -> putString(key, value)
                else -> remove(key)
            }
            apply()
        }
    }

    override fun getFloat(key: String): Float? {
        return when(prefs.contains(key)){
            true -> prefs.getFloat(key, 0f)
            false -> null
        }
    }

    override fun getString(key: String): String? {
        return prefs.getString(key, String())
    }
}