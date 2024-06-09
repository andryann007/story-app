package com.andryan.storyapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.andryan.storyapp.data.local.entity.LocalStoryItem

@Dao
interface LocalStoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalStory(story: LocalStoryItem)

    @Query("SELECT * FROM local_story")
    fun getAllLocalStory(): PagingSource<Int, LocalStoryItem>

    @Query("DELETE FROM local_story")
    suspend fun deleteAllLocalStory()
}