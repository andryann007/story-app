package com.andryan.storyapp.di

import android.content.Context
import androidx.room.Room
import com.andryan.storyapp.data.local.room.LocalStoryDao
import com.andryan.storyapp.data.local.room.LocalStoryDatabase
import com.andryan.storyapp.data.local.room.RemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideLocalStoryDatabase(@ApplicationContext context: Context): LocalStoryDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            LocalStoryDatabase::class.java,
            "db_local_story.db"
        ).build()
    }

    @Provides
    fun provideLocalStoryDao(localStoryDatabase: LocalStoryDatabase): LocalStoryDao =
        localStoryDatabase.localStoryDao()

    @Provides
    fun provideRemoteKeysDao(localStoryDatabase: LocalStoryDatabase): RemoteKeysDao =
        localStoryDatabase.remoteKeysDao()
}