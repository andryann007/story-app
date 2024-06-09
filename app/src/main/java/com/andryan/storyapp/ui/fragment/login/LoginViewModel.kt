package com.andryan.storyapp.ui.fragment.login

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.andryan.storyapp.data.AuthRepository
import com.andryan.storyapp.utils.Result
import com.andryan.storyapp.data.pref.UserModel
import com.andryan.storyapp.data.remote.response.LoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    suspend fun userLogin(email: String, password: String): Flow<Result<LoginResponse>> =
        authRepository.userLogin(email, password)

    suspend fun saveSession(user: UserModel) = authRepository.saveSession(user)
}