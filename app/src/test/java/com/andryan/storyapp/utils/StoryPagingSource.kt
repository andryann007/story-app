package com.andryan.storyapp.utils

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.andryan.storyapp.data.local.entity.LocalStoryItem

class StoryPagingSource : PagingSource<Int, LiveData<List<LocalStoryItem>>>() {
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<LocalStoryItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<LocalStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

    companion object {
        fun snapshot(items: List<LocalStoryItem>): PagingData<LocalStoryItem> {
            return PagingData.from(items)
        }
    }
}