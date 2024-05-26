package com.rifqi.sipalingstoryapp.data.response

data class StoryResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<Story>
)