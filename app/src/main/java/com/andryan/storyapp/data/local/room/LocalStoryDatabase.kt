package com.andryan.storyapp.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.andryan.storyapp.data.local.entity.LocalStoryItem
import com.andryan.storyapp.data.local.entity.RemoteKeys

@Database(entities = [LocalStoryItem::class, RemoteKeys::class], version = 2, exportSchema = false)
abstract class LocalStoryDatabase : RoomDatabase() {
    abstract fun localStoryDao(): LocalStoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}