package com.andryan.storyapp.data

import android.content.ContentValues.TAG
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.andryan.storyapp.data.local.entity.LocalStoryItem
import com.andryan.storyapp.data.local.room.LocalStoryDatabase
import com.andryan.storyapp.data.mediator.StoryRemoteMediator
import com.andryan.storyapp.data.remote.response.StoryDetailResponse
import com.andryan.storyapp.data.remote.response.StoryResponse
import com.andryan.storyapp.data.remote.response.UploadResponse
import com.andryan.storyapp.data.remote.retrofit.ApiService
import com.andryan.storyapp.utils.Result
import com.andryan.storyapp.utils.getBearerToken
import com.andryan.storyapp.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import javax.inject.Inject

@ExperimentalPagingApi
class StoryRepository @Inject constructor(
    private val apiService: ApiService,
    private val localStoryDatabase: LocalStoryDatabase
) {
    fun getAllStory(token: String): LiveData<PagingData<LocalStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(
                localStoryDatabase,
                apiService,
                getBearerToken(token)
            ),
            pagingSourceFactory = {
                localStoryDatabase.localStoryDao().getAllLocalStory()
            }
        ).liveData
    }

    suspend fun getStoriesWithLocation(token: String): Flow<Result<StoryResponse>> = flow {
        wrapEspressoIdlingResource {
            emit(Result.Loading)
            try {
                val bearerToken = getBearerToken(token)
                val story = apiService.getStoriesWithLocation(bearerToken, page = 1, size = 20)
                emit(Result.Success(story))
            } catch (e: Exception) {
                Timber.tag(TAG).d("getStoriesWithLocation: %s", e.message.toString())
                emit(Result.Error(e.message.toString()))
            }
        }
    }

    suspend fun getStoryDetail(token: String, id: String): LiveData<Result<StoryDetailResponse>> =
        liveData {
            wrapEspressoIdlingResource {
                emit(Result.Loading)
                try {
                    val bearerToken = getBearerToken(token)
                    val story = apiService.getStoryDetail(bearerToken, id)
                    emit(Result.Success(story))
                } catch (e: Exception) {
                    Timber.tag(TAG).d("getStoryDetail: %s", e.message.toString())
                    emit(Result.Error(e.message.toString()))
                }
            }
        }

    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): Flow<Result<UploadResponse>> = flow {
        wrapEspressoIdlingResource {
            emit(Result.Loading)
            try {
                val bearerToken = getBearerToken(token)
                val storyUpload = apiService.uploadStory(bearerToken, file, description, lat, lon)
                emit(Result.Success(storyUpload))
            } catch (e: Exception) {
                Timber.tag(TAG).d("uploadStory: %s", e.message.toString())
                emit(Result.Error(e.message.toString()))
            }
        }
    }
}