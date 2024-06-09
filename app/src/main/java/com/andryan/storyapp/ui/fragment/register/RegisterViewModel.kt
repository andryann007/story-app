package com.andryan.storyapp.ui.fragment.register

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.andryan.storyapp.data.AuthRepository
import com.andryan.storyapp.utils.Result
import com.andryan.storyapp.data.remote.response.RegisterResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    suspend fun userRegister(
        name: String,
        email: String,
        password: String
    ): Flow<Result<RegisterResponse>> =
        authRepository.userRegister(name, email, password)
}