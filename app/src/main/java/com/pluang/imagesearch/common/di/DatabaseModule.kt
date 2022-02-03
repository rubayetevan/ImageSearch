package com.pluang.imagesearch.common.di

import android.content.Context
import androidx.room.Room
import com.pluang.imagesearch.repository.local.AppDatabase
import com.pluang.imagesearch.repository.local.ResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "pluang"
        ).build()
    }

    @Provides
    fun provideChannelDao(appDatabase: AppDatabase): ResultDao {
        return appDatabase.imageDao()
    }
}