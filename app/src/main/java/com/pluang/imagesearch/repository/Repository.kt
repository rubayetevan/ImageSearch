package com.pluang.imagesearch.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.Nullable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.pluang.imagesearch.common.utility.IMAGE_EXTENSION
import com.pluang.imagesearch.common.utility.Resource
import com.pluang.imagesearch.models.ImageModel
import com.pluang.imagesearch.models.Result
import com.pluang.imagesearch.repository.local.UnsplashLocalDataSource
import com.pluang.imagesearch.repository.remote.UnsplashRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject


class Repository @Inject constructor(
    private val context: Context,
    private val unsplashLocalDataSource: UnsplashLocalDataSource,
    private val unsplashRemoteDataSource: UnsplashRemoteDataSource, private val externalScope: CoroutineScope
) {
    suspend fun getImages(query: String = "", page: Int = 1, perPage: Int = 10, hasInternet: Boolean): Flow<Resource<ImageModel>> {
        return if (hasInternet) getImagesFromRemoteDataSource(query, page, perPage) else getImagesFromLocalDataSource(query)
    }

    private suspend fun getImagesFromRemoteDataSource(query: String, page: Int, perPage: Int): Flow<Resource<ImageModel>> {
        return flow {
            emit(Resource.Loading<ImageModel>())
            val result = withContext(externalScope.coroutineContext) {
                unsplashRemoteDataSource.getImages(page = page, query = query, perPage = perPage)
            }
            if (result is Resource.Success) {
                if (result.data != null && result.data.total > 0) {
                    emit(Resource.Success<ImageModel>(result.data))
                    withContext(externalScope.coroutineContext) {
                        insertResultIntoLocalDB(result.data.results, query)
                    }
                } else {
                    emit(Resource.Empty<ImageModel>())
                }
            } else if (result is Resource.Error) {
                emit(Resource.Error<ImageModel>(result.message ?: "Error"))
            }
        }
    }

    private suspend fun getImagesFromLocalDataSource(query: String):
            Flow<Resource<ImageModel>> {
        return flow {
            emit(Resource.Loading<ImageModel>())
            val results = withContext(externalScope.coroutineContext) {
                unsplashLocalDataSource.getResultsByQuery(query)
            }
            if (results.isNotEmpty()) {
                val imageModel = ImageModel(
                    results = results,
                    total = results.size,
                    total_pages = 1
                )
                emit(Resource.Success<ImageModel>(imageModel))
            } else {
                emit(Resource.Empty<ImageModel>())
            }
        }
    }

    private suspend fun insertResultIntoLocalDB(results: List<Result>, queryString: String) {
        results.forEach {
            it.query = queryString
            downloadImage(it.urls.small, it.id)
        }
        unsplashLocalDataSource.insertAllResults(results)
    }

    private suspend fun downloadImage(imageURL: String, id: String) {
        withContext(externalScope.coroutineContext) {
            Glide.with(context)
                .load(imageURL)
                .into(object : CustomTarget<Drawable?>() {
                    override fun onResourceReady(resource: Drawable, @Nullable transition: Transition<in Drawable?>?) {
                        val bitmap = (resource as BitmapDrawable).bitmap
                        CoroutineScope(externalScope.coroutineContext).launch {
                            saveImage(bitmap, id)
                        }
                    }
                    override fun onLoadCleared(@Nullable placeholder: Drawable?) {}

                })

        }
    }

    private fun saveImage(bitmap: Bitmap, fileName: String) {
        val imageFile = File(context.filesDir, "$fileName$IMAGE_EXTENSION")
        val savedImagePath = imageFile.absolutePath
        Log.i("saveImage", savedImagePath)
        try {
            val outputStream: OutputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}