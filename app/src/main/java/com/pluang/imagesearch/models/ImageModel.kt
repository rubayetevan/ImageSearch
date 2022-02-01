package com.pluang.imagesearch.models

data class ImageModel(
    val results: List<Result>,
    val total: Int,
    val total_pages: Int
)