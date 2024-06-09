package com.andryan.storyapp.ui.activity.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import com.andryan.storyapp.data.AuthRepository
import com.andryan.storyapp.utils.Result
import com.andryan.storyapp.data.StoryRepository
import com.andryan.storyapp.data.pref.UserModel
import com.andryan.storyapp.data.remote.response.UploadResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import okhttp3.MultipartBody
import okhttp3.RequestBody

@ExperimentalPagingApi
@HiltViewModel
class UploadViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): LiveData<Result<UploadResponse>> =
        storyRepository.uploadStory(token, file, description, lat, lon).asLiveData()

    fun getSession(): Flow<UserModel> = authRepository.getSession()
}