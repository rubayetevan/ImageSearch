package com.pluang.imagesearch.views.fragments

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.pluang.imagesearch.common.utility.Validators
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

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
        }

        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val hasCellular = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            val hasWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            imageViewModel.hasInternet = hasCellular || hasWifi

        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            imageViewModel.hasInternet = false
        }
    }


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

        val connectivityManager = requireContext().getSystemService(ConnectivityManager::class.java)
        connectivityManager.requestNetwork(networkRequest, networkCallback)

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
            if (Validators.validateSearchQuery(imageViewModel.queryText)) {
                imageViewModel.totalPages = 0
                imageViewModel.isLoading = false
                imageViewModel.currentPageNumber = 1
                imageAdapter.clearData()
                subscribeQuery()
            } else {
                Toast.makeText(requireContext(), getString(R.string.empty_text_warning), Toast.LENGTH_SHORT).show()
            }
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
                        binding.progressBar.visibility = View.GONE
                        binding.textView.visibility = View.GONE
                        binding.imageRV.visibility = View.VISIBLE
                    }
                    is Resource.Loading -> {
                        if (imageViewModel.currentPageNumber == 1) {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.textView.visibility = View.GONE
                            binding.imageRV.visibility = View.GONE
                        }
                    }
                    is Resource.Error -> {
                        if (imageViewModel.currentPageNumber == 1) {
                            binding.progressBar.visibility = View.GONE
                            binding.textView.visibility = View.VISIBLE
                            binding.imageRV.visibility = View.GONE
                            binding.textView.text = it.message.toString()
                        }
                    }
                    is Resource.Empty -> {
                        if (imageViewModel.currentPageNumber == 1) {
                            binding.progressBar.visibility = View.GONE
                            binding.textView.visibility = View.VISIBLE
                            binding.imageRV.visibility = View.GONE
                            binding.textView.text = getString(R.string.empty_message)
                        }
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
        if (imageAdapter.gridSize == gridSize)
            return
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