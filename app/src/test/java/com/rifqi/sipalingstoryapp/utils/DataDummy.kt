package com.rifqi.sipalingstoryapp.utils

import com.rifqi.sipalingstoryapp.data.model.Story

object DataDummy {
    fun gnrDummyListStory(): List<Story> {
        val storyList: MutableList<Story> = arrayListOf()
        for (i in 0..10) {
            val stories = Story(
                "id $i",
                "Rifqi Lukmansyah",
                "test test test",
                "https://avatars.githubusercontent.com/u/1?v=4",
                "2024-05-01T00:00:00Z",
                "0.0f".toDouble(),
                "0.0f".toDouble()
            )
            storyList.add(stories)
        }
        return storyList
    }
}