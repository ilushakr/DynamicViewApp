package com.example.base.initializator

import android.content.Context
import org.koin.core.module.Module

abstract class AbstractInitializer {
    abstract fun create(context: Context, modules: (List<Module>) -> Unit)
}