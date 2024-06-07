package com.rifqi.sipalingstoryapp.di

import com.rifqi.sipalingstoryapp.data.repository.AppRepository
import com.rifqi.sipalingstoryapp.data.repository.AuthenticationRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { AuthenticationRepository(get()) }
    single { AppRepository(get()) }
}