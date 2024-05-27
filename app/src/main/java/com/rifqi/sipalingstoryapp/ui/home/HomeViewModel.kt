package com.rifqi.sipalingstoryapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rifqi.sipalingstoryapp.data.api.ApiService
import com.rifqi.sipalingstoryapp.data.model.Story
import com.rifqi.sipalingstoryapp.data.response.StoryResponse
import com.rifqi.sipalingstoryapp.preferences.ClientState
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class HomeViewModel (
    private val apiService: ApiService
) : ViewModel() {

    private val _stories = MutableLiveData<ClientState<List<Story>>>()
    val stories: LiveData<ClientState<List<Story>>> = _stories

    fun getStories() {
        viewModelScope.launch {
            try {
                _stories.postValue(ClientState.Loading())
                val response = apiService.getStories(page = 1, size = 50, location = null)

                if (response.error) {
                    _stories.postValue(ClientState.Error(response.message))
                } else {
                    _stories.postValue(ClientState.Success(response.listStory))
                }

            } catch (he: HttpException) {
                handleHttpException(he)
            } catch (e: Exception) {
                val errorMSG = when (e) {
                    is IOException -> "${e.message}"
                    else -> "Unknown error: ${e.message}"
                }
                _stories.postValue(ClientState.Error(errorMSG))
            }
        }
    }

    private fun handleHttpException(he: HttpException) {
        val jsonString = he.response()?.errorBody()?.string()
        try {
            val errorResponse = Gson().fromJson(jsonString, StoryResponse::class.java)
            val errorMSG = errorResponse?.message ?: "Unknown error"
            _stories.postValue(ClientState.Error(errorMSG))
        } catch (e: Exception) {
            _stories.postValue(ClientState.Error("Error when parsing response msg", null))
        }

    }
}


