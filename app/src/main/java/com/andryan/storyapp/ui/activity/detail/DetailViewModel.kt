package com.andryan.storyapp.ui.activity.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import com.andryan.storyapp.data.AuthRepository
import com.andryan.storyapp.utils.Result
import com.andryan.storyapp.data.StoryRepository
import com.andryan.storyapp.data.pref.UserModel
import com.andryan.storyapp.data.remote.response.StoryDetailResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    suspend fun getStoryDetail(token: String, id: String): LiveData<Result<StoryDetailResponse>> =
        storyRepository.getStoryDetail(token, id).asLiveData()

    fun getSession(): Flow<UserModel> = authRepository.getSession()
}