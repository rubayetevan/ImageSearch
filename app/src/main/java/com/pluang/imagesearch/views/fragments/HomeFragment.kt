package com.pluang.imagesearch.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.pluang.imagesearch.common.utility.Resource
import com.pluang.imagesearch.databinding.FragmentHomeBinding
import com.pluang.imagesearch.viewModels.ImageViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val imageViewModel: ImageViewModel by activityViewModels<ImageViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeQuery()
    }

    private fun subscribeQuery() {
        lifecycleScope.launch {
            val result = imageViewModel.getImages()
            result.collect {
                when (it) {
                    is Resource.Success -> {
                        Log.d("handleUiState", "Resource.Success-> " + it.data.toString())
                    }
                    is Resource.Loading -> {
                        Log.d("handleUiState", "Resource.Loading-> " + it.message.toString())
                    }
                    is Resource.Error -> {
                        Log.d("handleUiState", "Resource.Error-> " + it.message.toString())
                    }
                    is Resource.Empty -> {
                        Log.d("handleUiState", "Resource.Empty-> ")
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}