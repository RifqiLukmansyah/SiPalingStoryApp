package com.rifqi.sipalingstoryapp.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.rifqi.sipalingstoryapp.di.appModule
import com.rifqi.sipalingstoryapp.di.repositoryModule
import com.rifqi.sipalingstoryapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule, viewModelModule, repositoryModule)
        }
    }
}