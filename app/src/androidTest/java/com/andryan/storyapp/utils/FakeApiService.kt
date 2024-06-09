package com.andryan.storyapp.utils

import com.andryan.storyapp.data.remote.response.ListStoryItem
import com.andryan.storyapp.data.remote.response.LoginResponse
import com.andryan.storyapp.data.remote.response.RegisterResponse
import com.andryan.storyapp.data.remote.response.StoryDetailResponse
import com.andryan.storyapp.data.remote.response.StoryResponse
import com.andryan.storyapp.data.remote.response.UploadResponse
import com.andryan.storyapp.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApiService : ApiService {
    override suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): RegisterResponse {
        TODO("Not yet implemented")
    }

    override suspend fun loginUser(email: String, password: String): LoginResponse {
        TODO("Not yet implemented")
    }

    override suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): UploadResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getAllStory(
        token: String,
        page: Int,
        size: Int,
        location: Int?
    ): StoryResponse {
        val error = false
        val message = "Success to load story !!!"
        val listStory = mutableListOf<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                id = "story-FvU4u0vU4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                name = "Dimas",
                description = "Lorem Ipsum",
                lat = -10.212,
                lon = -16.002
            )
            listStory.add(story)
        }
        return StoryResponse(listStory, error, message)
    }

    override suspend fun getStoryDetail(token: String, id: String): StoryDetailResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getStoriesWithLocation(
        token: String,
        page: Int?,
        size: Int?,
        location: Int
    ): StoryResponse {
        TODO("Not yet implemented")
    }

}