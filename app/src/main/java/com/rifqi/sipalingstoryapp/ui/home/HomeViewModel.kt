package com.rifqi.sipalingstoryapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rifqi.sipalingstoryapp.data.model.Story
import com.rifqi.sipalingstoryapp.data.repository.AppRepository

class HomeViewModel (
    storyRepository: AppRepository,
) : ViewModel() {

    val story: LiveData<PagingData<Story>> = storyRepository.getStory().cachedIn(viewModelScope)




}


