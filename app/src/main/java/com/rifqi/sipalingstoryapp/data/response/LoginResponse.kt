package com.rifqi.sipalingstoryapp.data.response

import com.rifqi.sipalingstoryapp.data.model.LoginResult

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult?
)