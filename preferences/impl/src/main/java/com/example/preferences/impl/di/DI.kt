package com.example.preferences.impl.di

import android.content.Context
import com.example.preferences.api.PreferencesProvider
import com.example.preferences.impl.PreferencesProviderImpl
import com.example.preferences.impl.R
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val preferencesModule = module {

    single<PreferencesProvider> {
        val prefs = with(androidApplication()){
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        }
        PreferencesProviderImpl(prefs)
    }

}