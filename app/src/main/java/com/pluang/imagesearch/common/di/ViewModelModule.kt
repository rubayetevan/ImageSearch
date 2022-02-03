package com.pluang.imagesearch.common.di

import android.content.Context
import com.pluang.imagesearch.repository.Repository
import com.pluang.imagesearch.repository.local.UnsplashLocalDataSource
import com.pluang.imagesearch.repository.remote.UnsplashRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    fun provideRepository(
        @ApplicationContext appContext: Context,
        externalScope: CoroutineScope,
        unsplashLocalDataSource: UnsplashLocalDataSource,
        unsplashRemoteDataSource: UnsplashRemoteDataSource
    ): Repository {
        return Repository(appContext,unsplashLocalDataSource,unsplashRemoteDataSource,externalScope)
    }

}