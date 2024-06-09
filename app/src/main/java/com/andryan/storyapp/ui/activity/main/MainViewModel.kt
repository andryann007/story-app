package com.andryan.storyapp.ui.activity.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andryan.storyapp.data.AuthRepository
import com.andryan.storyapp.data.StoryRepository
import com.andryan.storyapp.data.local.entity.LocalStoryItem
import com.andryan.storyapp.data.pref.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllStory(token: String): LiveData<PagingData<LocalStoryItem>> =
        storyRepository.getAllStory(token).cachedIn(viewModelScope)

    fun getSession(): Flow<UserModel> = authRepository.getSession()

    suspend fun logout() = authRepository.logout()
}