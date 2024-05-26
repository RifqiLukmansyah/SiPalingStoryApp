package com.rifqi.sipalingstoryapp.data.response

import com.rifqi.sipalingstoryapp.data.model.Story

data class DetailResponse(
    val error: Boolean,
    val message: String,
    val story: Story
)