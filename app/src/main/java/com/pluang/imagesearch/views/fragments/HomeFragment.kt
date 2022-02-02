package com.pluang.imagesearch.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.pluang.imagesearch.R
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
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (imageViewModel.fragmentHomeBinding == null) {
            imageViewModel.fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
            _binding = imageViewModel.fragmentHomeBinding
            binding.imageViewModel = imageViewModel
            imageAdapter = ImageAdapter(requireContext())
        }
        _binding = imageViewModel.fragmentHomeBinding
        sharedElementReturnTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.explode)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener {
            Log.d("handleUiState", "binding.button.setOnClickListener-> ${imageViewModel.queryText}")
        }


        binding.imageRV.apply {
            val gridLayoutManager = GridLayoutManager(context, imageAdapter.gridSize)
            layoutManager = gridLayoutManager
            adapter = imageAdapter
            setHasFixedSize(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    if (!imageViewModel.isLoading) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == imageAdapter.getCurrentDataSize - 1) {
                            //bottom of list!
                            loadMore()
                            imageViewModel.isLoading = true

                        }
                    }
                }
            })
        }
        postponeEnterTransition()
        binding.imageRV.doOnPreDraw {
            startPostponedEnterTransition()
        }

        subscribeQuery()

        binding.button.setOnClickListener {
            imageViewModel.totalPages = 0
            imageViewModel.isLoading = false
            imageViewModel.currentPageNumber = 1
            imageAdapter.clearData()
            subscribeQuery()
        }
    }

    private fun subscribeQuery(page: Int = 1) {
        lifecycleScope.launch {
            val result = imageViewModel.getImages(page = page)
            result.collect {
                when (it) {
                    is Resource.Success -> {
                        Log.d("handleUiState", "Resource.Success-> " + it.data.toString())
                        imageViewModel.totalPages = it.data?.total_pages ?: 0
                        it.data?.results?.let { results ->
                            imageAdapter.addData(results)
                        }
                        imageViewModel.isLoading = false
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

    private fun loadMore() {
        imageViewModel.currentPageNumber += 1
        if (imageViewModel.currentPageNumber <= imageViewModel.totalPages) {
            subscribeQuery(imageViewModel.currentPageNumber)
            imageAdapter.isLastPage = false
        } else {
            imageAdapter.isLastPage = true
            imageAdapter.notifyItemChanged(imageAdapter.getCurrentDataSize - 1)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_two -> {
                changeGridSize(2)
                true
            }
            R.id.menu_item_three -> {
                changeGridSize(3)
                true
            }
            R.id.menu_item_four -> {
                changeGridSize(4)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun changeGridSize(gridSize: Int) {
        imageAdapter.gridSize = gridSize
        val gridLayoutManager = GridLayoutManager(context, gridSize)
        binding.imageRV.layoutManager = gridLayoutManager
        imageAdapter.notifyItemChanged(imageAdapter.getCurrentDataSize / 2)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}