package com.pluang.imagesearch.repository.local

import androidx.room.*
import com.pluang.imagesearch.models.Result

@Dao
interface ResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(data:Result)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllResults(data:List<Result>)

    @Update
    suspend fun updateResult(data: Result)

    @Delete
    suspend fun deleteResult(data: Result)
}