package com.example.design.provider.impl.di

import com.example.design.provider.impl.ResourceProviderImpl
import com.example.design.provider.api.ResourceProvider
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

internal val providerModule = module {

    single<ResourceProvider> {
        ResourceProviderImpl(androidApplication(), get())
    }

}