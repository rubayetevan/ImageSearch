package com.pluang.imagesearch.common.di

import com.pluang.imagesearch.repository.Repository
import com.pluang.imagesearch.repository.remote.UnsplashRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    fun provideRepository(
        externalScope: CoroutineScope,
        unsplashRemoteDataSource: UnsplashRemoteDataSource
    ): Repository {
        return Repository(unsplashRemoteDataSource,externalScope)
    }

}