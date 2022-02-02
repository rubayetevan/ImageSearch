package com.pluang.imagesearch.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import com.pluang.imagesearch.common.utility.Resource
import com.pluang.imagesearch.databinding.FragmentHomeBinding
import com.pluang.imagesearch.viewModels.ImageViewModel
import com.pluang.imagesearch.views.adapters.ImageAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val imageViewModel: ImageViewModel by activityViewModels<ImageViewModel>()
    private val imageAdapter = ImageAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.imageViewModel = imageViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener {
            Log.d("handleUiState", "binding.button.setOnClickListener-> ${imageViewModel.queryText}")
        }


        binding.imageRV.apply {
            val onSpanSizeLookup: SpanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return 2
                }
            }
            val gridLayoutManager = GridLayoutManager(context,4)
            gridLayoutManager.spanSizeLookup = onSpanSizeLookup
            layoutManager = gridLayoutManager
            adapter = imageAdapter
        }
        subscribeQuery()
        binding.button.setOnClickListener {
            imageAdapter.clearData()
            subscribeQuery()
        }


    }

    private fun subscribeQuery() {
        lifecycleScope.launch {
            val result = imageViewModel.getImages()
            result.collect {
                when (it) {
                    is Resource.Success -> {
                        Log.d("handleUiState", "Resource.Success-> " + it.data.toString())
                        it.data?.results?.let {results->
                            imageAdapter.addData(results)
                        }
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