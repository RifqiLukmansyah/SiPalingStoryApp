package com.rifqi.sipalingstoryapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rifqi.sipalingstoryapp.data.model.Story
import com.rifqi.sipalingstoryapp.data.repository.AppRepository
import com.rifqi.sipalingstoryapp.preferences.ClientState
import com.rifqi.sipalingstoryapp.data.response.StoryResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MapsViewModel(
private val appRepo: AppRepository
) : ViewModel() {
    private val _map = MutableLiveData<ClientState<List<Story>>>()
    val map: LiveData<ClientState<List<Story>>> = _map

    fun mapStory() {
        viewModelScope.launch {
            try {
                _map.postValue(ClientState.Loading())
                val response = appRepo.getLocation()

                if (response.error) {
                    _map.postValue(ClientState.Error(response.message))
                } else {
                    _map.postValue(ClientState.Success(response.listStory))
                }
            } catch (he: HttpException) {
                handleHttpException(he)
            } catch (e: Exception) {
                val errorMSG = when (e) {
                    is IOException -> "${e.message}"
                    else -> "Unknown error: ${e.message}"
                }
                _map.postValue(ClientState.Error(errorMSG))
            }
        }
    }

    private fun handleHttpException(he: HttpException) {
        val jsonString = he.response()?.errorBody()?.string()
        try {
            val errorResponse = Gson().fromJson(jsonString, StoryResponse::class.java)
            val errorMSG = errorResponse?.message ?: "Unknown error"
            _map.postValue(ClientState.Error(errorMSG))
        } catch (e: Exception) {
            _map.postValue(ClientState.Error("Error when parsing response msg", null))
        }

    }
}
