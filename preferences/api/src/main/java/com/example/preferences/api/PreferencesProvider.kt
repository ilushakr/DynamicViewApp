package com.example.preferences.api

interface PreferencesProvider {

    fun setValue(key: String, value: Any?)

    fun getFloat(key: String): Float?

    fun getString(key: String): String?

}