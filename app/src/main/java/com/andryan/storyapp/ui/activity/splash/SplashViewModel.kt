package com.andryan.storyapp.ui.activity.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import com.andryan.storyapp.data.AuthRepository
import com.andryan.storyapp.data.pref.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    fun getSession(): LiveData<UserModel> = authRepository.getSession().asLiveData()
}