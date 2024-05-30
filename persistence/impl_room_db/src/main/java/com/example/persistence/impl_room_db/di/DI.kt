package com.example.persistence.impl_room_db.di

import com.example.persistence.api.PersistenceProvider
import com.example.persistence.impl_room_db.PersistenceProviderImpl
import org.koin.dsl.module

val preferencesModule = module {

    single<PersistenceProvider> {
        PersistenceProviderImpl()
    }

}