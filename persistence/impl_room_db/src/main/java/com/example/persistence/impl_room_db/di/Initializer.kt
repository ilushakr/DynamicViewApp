package com.example.persistence.impl_room_db.di

import android.content.Context
import com.example.base.initializator.AbstractInitializer
import com.example.persistence.impl_room_db.di.preferencesModule
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