package com.rifqi.sipalingstoryapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rifqi.sipalingstoryapp.data.api.ApiService
import com.rifqi.sipalingstoryapp.data.model.Story
import com.rifqi.sipalingstoryapp.data.response.DetailResponse
import com.rifqi.sipalingstoryapp.preferences.ClientState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DetailViewModel (
    private val apiService: ApiService
) : ViewModel() {

    private val _detail = MutableLiveData<ClientState<Story>>()
    val detail: LiveData<ClientState<Story>> = _detail

    fun detail(storyId: String) {
        viewModelScope.launch {
            try {
                _detail.postValue(ClientState.Loading())
                val response = apiService.getDetail(storyId)

                if (response.error) {
                    _detail.postValue(ClientState.Error(response.message))
                } else {
                    _detail.postValue(ClientState.Success(response.story))
                }

            } catch (he: HttpException) {
                handleHttpException(he)
            } catch (e: Exception) {
                val errorMSG = when (e) {
                    is IOException -> "${e.message}"
                    else -> "Unknown error: ${e.message}"
                }
                _detail.postValue(ClientState.Error(errorMSG))
            }
        }
    }

    private fun handleHttpException(he: HttpException) {
        val jsonString = he.response()?.errorBody()?.string()
        try {
            val errorResponse = Gson().fromJson(jsonString, DetailResponse::class.java)
            val errorMSG = errorResponse?.message ?: "Unknown error"
            _detail.postValue(ClientState.Error(errorMSG))
        } catch (e: Exception) {
            _detail.postValue(ClientState.Error("Error when parsing response msg", null))
        }

    }

}