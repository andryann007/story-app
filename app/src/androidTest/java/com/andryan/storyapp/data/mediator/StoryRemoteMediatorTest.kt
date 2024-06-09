package com.andryan.storyapp.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.andryan.storyapp.data.local.entity.LocalStoryItem
import com.andryan.storyapp.data.local.room.LocalStoryDatabase
import com.andryan.storyapp.data.remote.retrofit.ApiService
import com.andryan.storyapp.utils.FakeApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {
    private var mockApi: ApiService = FakeApiService()

    private var mockDatabase: LocalStoryDatabase = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        LocalStoryDatabase::class.java
    ).allowMainThreadQueries().build()

    private val dummyToken = "auth_token"

    private lateinit var storyRemoteMediator: StoryRemoteMediator

    @Before
    fun setUp() {
        storyRemoteMediator = StoryRemoteMediator(mockDatabase, mockApi, dummyToken)
    }

    @After
    fun tearDown() {
        mockDatabase.clearAllTables()
    }

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val pagingState = PagingState<Int, LocalStoryItem>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = storyRemoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }
}