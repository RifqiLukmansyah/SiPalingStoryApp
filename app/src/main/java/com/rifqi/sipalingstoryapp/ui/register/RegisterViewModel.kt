package com.rifqi.sipalingstoryapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rifqi.sipalingstoryapp.data.repository.AuthenticationRepository
import com.rifqi.sipalingstoryapp.data.response.RegisterResponse
import com.rifqi.sipalingstoryapp.preferences.ClientState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegisterViewModel(
    private val apiService: AuthenticationRepository
) : ViewModel() {
    private val _register = MutableLiveData<ClientState<RegisterResponse>>()
    val register: LiveData<ClientState<RegisterResponse>> = _register

    fun performRegister(name: String, email: String, pass: String) {
        viewModelScope.launch {
            try {
                _register.postValue(ClientState.Loading())
                val response = apiService.register(name, email, pass)

                if (response.error) {
                    _register.postValue(ClientState.Error(response.message))
                } else {
                    _register.postValue(ClientState.Success(response))
                }

            } catch (he: HttpException) {
                handleHttpException(he)
            } catch (e: Exception) {
                val errorMSG = when (e) {
                    is IOException -> "${e.message}"
                    else -> "Unknown error: ${e.message}"
                }
                _register.postValue(ClientState.Error(errorMSG))
            }
        }
    }

    private fun handleHttpException(he: HttpException) {
        val jsonString = he.response()?.errorBody()?.string()
        try {
            val errorResponse = Gson().fromJson(jsonString, RegisterResponse::class.java)
            val errorMSG = errorResponse?.message ?: "Unknown error"
            _register.postValue(ClientState.Error(errorMSG))
        } catch (e: Exception) {
            _register.postValue(ClientState.Error("Error when parsing response msg", null))
        }
    }
}