package com.pluang.imagesearch.repository.local

import javax.inject.Inject
import com.pluang.imagesearch.models.Result

class UnsplashLocalDataSource @Inject constructor(private val  resultDao: ResultDao) {

    suspend fun insertAllResults(results: List<Result>){
        resultDao.insertAllResults(results)
    }


}