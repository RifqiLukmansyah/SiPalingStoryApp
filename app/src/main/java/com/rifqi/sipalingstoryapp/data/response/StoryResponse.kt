package com.rifqi.sipalingstoryapp.data.response

import com.rifqi.sipalingstoryapp.data.model.Story

data class StoryResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<Story>
)