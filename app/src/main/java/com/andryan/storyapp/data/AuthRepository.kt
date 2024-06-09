package com.andryan.storyapp.data

import android.content.ContentValues.TAG
import com.andryan.storyapp.data.pref.UserModel
import com.andryan.storyapp.data.pref.UserPreferences
import com.andryan.storyapp.data.remote.response.LoginResponse
import com.andryan.storyapp.data.remote.response.RegisterResponse
import com.andryan.storyapp.data.remote.retrofit.ApiService
import com.andryan.storyapp.utils.Result
import com.andryan.storyapp.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {
    suspend fun userRegister(
        name: String,
        email: String,
        password: String
    ): Flow<Result<RegisterResponse>> = flow {
        wrapEspressoIdlingResource {
            emit(Result.Loading)
            try {
                val register = apiService.registerUser(name, email, password)
                emit(Result.Success(register))
            } catch (e: Exception) {
                Timber.tag(TAG).d("userRegister: %s", e.message.toString())
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun userLogin(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        wrapEspressoIdlingResource {
            emit(Result.Loading)
            try {
                val login = apiService.loginUser(email, password)
                emit(Result.Success(login))
            } catch (e: Exception) {
                Timber.tag(TAG).d("userLogin: %s", e.message.toString())
                emit(Result.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun saveSession(user: UserModel) =
        wrapEspressoIdlingResource { userPreferences.saveSession(user) }

    fun getSession(): Flow<UserModel> {
        return userPreferences.getSession()
    }

    suspend fun logout() = wrapEspressoIdlingResource { userPreferences.logout() }
}