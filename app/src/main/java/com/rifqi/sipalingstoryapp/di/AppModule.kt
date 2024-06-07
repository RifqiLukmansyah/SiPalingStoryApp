package com.rifqi.sipalingstoryapp.di

import org.koin.dsl.module
import com.rifqi.sipalingstoryapp.data.api.ApiConfig
import com.rifqi.sipalingstoryapp.preferences.UserPreferences
import org.koin.android.ext.koin.androidContext

val appModule = module {

    single { ApiConfig.getApiService() }
    single { UserPreferences.getInstance(androidContext()) }
}