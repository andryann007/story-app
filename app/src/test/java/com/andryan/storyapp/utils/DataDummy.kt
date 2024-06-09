package com.andryan.storyapp.utils

import com.andryan.storyapp.data.local.entity.LocalStoryItem

object DataDummy {
    fun generateDummyLocalStories(): List<LocalStoryItem> {
        val items: MutableList<LocalStoryItem> = arrayListOf()
        for (i in 0..10) {
            val story = LocalStoryItem(
                id = "story-FvU4u0vU4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                name = "Dimas",
                description = "Lorem Ipsum",
                lat = -10.212,
                lon = -16.002
            )
            items.add(story)
        }
        return items
    }
}