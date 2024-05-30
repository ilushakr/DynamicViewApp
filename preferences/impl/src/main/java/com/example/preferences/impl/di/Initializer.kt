package com.example.preferences.impl.di

import android.content.Context
import com.example.base.initializator.AbstractInitializer
import org.koin.core.module.Module

class Initializer : AbstractInitializer() {
    override fun create(context: Context, modules: (List<Module>) -> Unit) {
        modules(
            listOf(
                preferencesModule
            )
        )
    }
}