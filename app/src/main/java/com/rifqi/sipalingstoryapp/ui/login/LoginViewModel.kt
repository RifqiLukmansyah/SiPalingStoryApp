package com.rifqi.sipalingstoryapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import com.rifqi.sipalingstoryapp.data.model.LoginResult
import com.rifqi.sipalingstoryapp.data.repository.AuthenticationRepository
import com.rifqi.sipalingstoryapp.data.response.LoginResponse
import com.rifqi.sipalingstoryapp.preferences.ClientState

class LoginViewModel (
    private val apiService: AuthenticationRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<ClientState<LoginResult>>()
    val loginResult: LiveData<ClientState<LoginResult>> = _loginResult


    fun performLogin(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loginResult.postValue(ClientState.Loading())
                val response = apiService.login(email, password)

                if (response.error) {
                    _loginResult.postValue(ClientState.Error(response.message))
                } else {
                    _loginResult.postValue(ClientState.Success(response.loginResult!!))
                }
            } catch (he: HttpException) {
                handleHttpException(he)
            } catch (e: Exception) {
                val errorMSG = when (e) {
                    is IOException -> "${e.message}"
                    else -> "Unknown error: ${e.message}"
                }
                _loginResult.postValue(ClientState.Error(errorMSG))
            }
        }
    }

    private fun handleHttpException(he: HttpException) {
        val jsonString = he.response()?.errorBody()?.string()
        try {
            val errorResponse = Gson().fromJson(jsonString, LoginResponse::class.java)
            val errorMSG = errorResponse?.message ?: "Unknown error"
            _loginResult.postValue(ClientState.Error(errorMSG))
        } catch (e: Exception) {
            _loginResult.postValue(ClientState.Error("Error when parsing response msg", null))
        }

    }
}

