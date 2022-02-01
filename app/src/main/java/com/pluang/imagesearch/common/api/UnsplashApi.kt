package com.pluang.imagesearch.common.api

import com.pluang.imagesearch.common.utility.CLIENT_ID
import com.pluang.imagesearch.models.ImageModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {

    @GET("search/photos?client_id=$CLIENT_ID")
    suspend fun getImages(
        @Query("query") query: String? = "",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
    ): Response<ImageModel>
}