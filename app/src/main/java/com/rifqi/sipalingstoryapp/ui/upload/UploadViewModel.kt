package com.rifqi.sipalingstoryapp.ui.upload

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rifqi.sipalingstoryapp.data.repository.AppRepository
import com.rifqi.sipalingstoryapp.data.response.AddStoryResponse
import com.rifqi.sipalingstoryapp.preferences.ClientState
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException

class UploadViewModel (
    private val appRepo: AppRepository
) : ViewModel() {
    private val _addStory = MutableLiveData<ClientState<AddStoryResponse>>()
    val addStory: LiveData<ClientState<AddStoryResponse>> = _addStory

    private val _photoPath = MutableLiveData<Uri>()
    val photoPath: LiveData<Uri> = _photoPath

    fun uploadStory(
        photoPart: MultipartBody.Part,
        descPart: RequestBody,
        lat: Double,
        lon: Double

        ) {
        viewModelScope.launch {
            try {
                _addStory.postValue(ClientState.Loading())
                val response = appRepo.upload(photoPart, descPart, lat, lon)

                _addStory.postValue(
                    if (response.error)
                        ClientState.Error(response.message)
                    else
                        ClientState.Success(response)
                )

            } catch (he: HttpException) {
                handleHttpException(he)
            } catch (e: Exception) {
                val errorMSG = when (e) {
                    is IOException -> "${e.message}"
                    else -> "Unknown error: ${e.message}"
                }
                _addStory.postValue(ClientState.Error(errorMSG))
            }
        }
    }

    private fun handleHttpException(he: HttpException) {
        val jsonString = he.response()?.errorBody()?.string()
        try {
            val errorResponse = Gson().fromJson(jsonString, AddStoryResponse::class.java)
            val errorMSG = errorResponse?.message ?: "Unknown error"
            _addStory.postValue(ClientState.Error(errorMSG))
        } catch (e: Exception) {
            _addStory.postValue(ClientState.Error("Error when parsing response msg", null))
        }

    }

}