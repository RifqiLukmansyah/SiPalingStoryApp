package com.rifqi.sipalingstoryapp.di

import com.rifqi.sipalingstoryapp.ui.detail.DetailViewModel
import com.rifqi.sipalingstoryapp.ui.home.HomeViewModel
import com.rifqi.sipalingstoryapp.ui.login.LoginViewModel
import com.rifqi.sipalingstoryapp.ui.register.RegisterViewModel
import com.rifqi.sipalingstoryapp.ui.upload.UploadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { UploadViewModel(get()) }
}