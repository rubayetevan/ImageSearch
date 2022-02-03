package com.pluang.imagesearch.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Result(
    @ColumnInfo val created_at: String,
    @ColumnInfo val height: Int,
    @PrimaryKey @ColumnInfo val id: String,
    @Embedded val urls: Urls,
    @ColumnInfo val width: Int,
    @ColumnInfo var query: String,
)