package com.rifqi.sipalingstoryapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.rifqi.sipalingstoryapp.data.api.ApiService
import com.rifqi.sipalingstoryapp.data.model.Story
import com.rifqi.sipalingstoryapp.data.paging.StoryRemoteMediator
import com.rifqi.sipalingstoryapp.data.paging.PagingSource
import com.rifqi.sipalingstoryapp.data.response.AddStoryResponse
import com.rifqi.sipalingstoryapp.data.response.DetailResponse
import com.rifqi.sipalingstoryapp.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AppRepository (
    private val apiService: ApiService
) {

    suspend fun detail(id: String): DetailResponse {
        return apiService.getDetail(id)
    }

    suspend fun getLocation(): StoryResponse {
        return apiService.getStoriesLocation()
    }

    suspend fun upload(
        img: MultipartBody.Part,
        desc: RequestBody,
        lat: Double,
        lon: Double
    ): AddStoryResponse {
        return apiService.uploadStory(img, desc, lat, lon)
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStory(): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 1
            ),
            remoteMediator = StoryRemoteMediator(apiService),
            pagingSourceFactory = {
                PagingSource(apiService)
            }
        ).liveData
    }
}