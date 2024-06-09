package com.andryan.storyapp.ui.activity.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.andryan.storyapp.data.AuthRepository
import com.andryan.storyapp.data.StoryRepository
import com.andryan.storyapp.data.local.entity.LocalStoryItem
import com.andryan.storyapp.utils.DataDummy
import com.andryan.storyapp.utils.MainDispatcherRule
import com.andryan.storyapp.utils.StoryListAdapter
import com.andryan.storyapp.utils.StoryPagingSource
import com.andryan.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get: Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mainViewModel: MainViewModel

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var authRepository: AuthRepository

    private val dummyToken = "auth_token"

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(storyRepository, authRepository)
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyLocalStories()
        val data: PagingData<LocalStoryItem> = StoryPagingSource.snapshot(dummyStories)

        val expectedStories = MutableLiveData<PagingData<LocalStoryItem>>()
        expectedStories.value = data
        `when`(storyRepository.getAllStory(dummyToken)).thenReturn(expectedStories)

        val actualStories = mainViewModel.getAllStory(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        verify(storyRepository).getAllStory(dummyToken)
        verifyNoInteractions(authRepository)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<LocalStoryItem> = PagingData.from(emptyList())

        val expectedStories = MutableLiveData<PagingData<LocalStoryItem>>()
        expectedStories.value = data
        `when`(storyRepository.getAllStory(dummyToken)).thenReturn(expectedStories)

        val actualStories = mainViewModel.getAllStory(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        verify(storyRepository).getAllStory(dummyToken)
        verifyNoInteractions(authRepository)

        Assert.assertEquals(0, differ.snapshot().size)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}

        override fun onRemoved(position: Int, count: Int) {}

        override fun onMoved(fromPosition: Int, toPosition: Int) {}

        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}