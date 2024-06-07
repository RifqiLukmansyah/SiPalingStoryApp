package com.rifqi.sipalingstoryapp.data.repository

import com.rifqi.sipalingstoryapp.data.api.ApiService
import com.rifqi.sipalingstoryapp.data.response.LoginResponse
import com.rifqi.sipalingstoryapp.data.response.RegisterResponse

class AuthenticationRepository(
    private val apiService: ApiService
) {

    suspend fun login(email: String, pass: String): LoginResponse {
        return apiService.login(email, pass)
    }

    suspend fun register(name: String, email: String, pass: String): RegisterResponse {
        return apiService.register(name, email, pass)
    }

}