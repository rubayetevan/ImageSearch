package com.pluang.imagesearch.common.di

import com.pluang.imagesearch.common.api.UnsplashApi
import com.pluang.imagesearch.repository.remote.UnsplashRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun provideRemoteDataSource(
        unsplashApi: UnsplashApi
    ): UnsplashRemoteDataSource {
        return UnsplashRemoteDataSource(unsplashApi)
    }
}