package com.rifqi.sipalingstoryapp.di

import org.koin.dsl.module
import com.rifqi.sipalingstoryapp.data.api.ApiConfig

val storyModule = module {

    single { ApiConfig.getApiService() }
}