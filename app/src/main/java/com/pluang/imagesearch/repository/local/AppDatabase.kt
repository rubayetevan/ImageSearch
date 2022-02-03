package com.pluang.imagesearch.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pluang.imagesearch.models.Result

@Database(entities = [Result::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ResultDao
}