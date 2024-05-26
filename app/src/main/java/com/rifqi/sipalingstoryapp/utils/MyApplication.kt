package com.rifqi.sipalingstoryapp.utils

import android.app.Application
import com.rifqi.sipalingstoryapp.data.api.ApiConfig
import com.rifqi.sipalingstoryapp.di.appModule
import com.rifqi.sipalingstoryapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule, viewModelModule)
        }
    }
}