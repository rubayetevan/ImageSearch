package com.pluang.imagesearch.repository

import com.pluang.imagesearch.common.utility.Resource
import com.pluang.imagesearch.models.ImageModel
import com.pluang.imagesearch.repository.remote.UnsplashRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
    private val unsplashRemoteDataSource: UnsplashRemoteDataSource, private val externalScope: CoroutineScope
) {
    suspend fun getImages(query: String = "", page: Int = 1, perPage: Int = 10): Flow<Resource<ImageModel>> {
        return flow {
            emit(Resource.Loading<ImageModel>())
            val result = withContext(externalScope.coroutineContext) {
                unsplashRemoteDataSource.getImages(page = page, query = query, perPage = perPage)
            }
            if (result is Resource.Success) {
                if (result.data != null) {
                    emit(Resource.Success<ImageModel>(result.data))
                } else {
                    emit(Resource.Empty<ImageModel>())
                }

            } else if (result is Resource.Error) {
                emit(Resource.Error<ImageModel>(result.message ?: "Error"))
            }
        }
    }

}