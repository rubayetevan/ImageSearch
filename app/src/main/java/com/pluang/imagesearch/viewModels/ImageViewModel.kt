package com.pluang.imagesearch.viewModels

import androidx.lifecycle.ViewModel
import com.pluang.imagesearch.common.utility.Resource
import com.pluang.imagesearch.databinding.FragmentHomeBinding
import com.pluang.imagesearch.models.ImageModel
import com.pluang.imagesearch.models.Result
import com.pluang.imagesearch.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    var fragmentHomeBinding: FragmentHomeBinding? = null

    var queryText = "Indonesia"
    var currentPageNumber = 1
    var totalPages = 0
    var isLoading = false

    suspend fun getImages(page: Int = 1, perPage: Int = 10): Flow<Resource<ImageModel>> =
        repository.getImages(page = page, query = queryText, perPage = perPage)

}