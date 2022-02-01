package com.pluang.imagesearch.repository.remote

import com.pluang.imagesearch.common.api.UnsplashApi
import com.pluang.imagesearch.common.utility.Resource
import com.pluang.imagesearch.models.ImageModel
import javax.inject.Inject

class UnsplashRemoteDataSource @Inject constructor(private val unsplashApi: UnsplashApi) {

    suspend fun getImages(query: String = "", page: Int = 1, perPage: Int = 10): Resource<ImageModel?> {
        return try {
            val response = unsplashApi.getImages(page = page, query = query, perPage = perPage)
            if (response.isSuccessful) {
                Resource.Success(response.body())
            } else {
                Resource.Error(response.errorBody().toString())
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Could not get jobs data.")
        }
    }

}