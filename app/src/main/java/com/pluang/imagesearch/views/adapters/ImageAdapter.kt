package com.pluang.imagesearch.views.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pluang.imagesearch.R
import com.pluang.imagesearch.common.utility.IMAGE_EXTENSION
import com.pluang.imagesearch.common.utility.physicalScreenRectPx
import com.pluang.imagesearch.databinding.ItemImageBinding
import com.pluang.imagesearch.models.Result
import java.io.File


class ImageAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data: ArrayList<Result> = ArrayList<Result>()
    val getCurrentDataSize get() = data.size
    var isLastPage = false
    var gridSize = 2


    private inner class ImageViewHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageViewHolder(
            ItemImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        populateData(holder as ImageViewHolder, position)
    }

    private fun populateData(imageViewHolder: ImageViewHolder, position: Int) {
        val physicalWidthPx = context.physicalScreenRectPx.width()
        //val physicalHeightPx = context.physicalScreenRectPx.height()
        imageViewHolder.binding.imageView.layoutParams.width = physicalWidthPx / gridSize
        val url = data[position].urls.small
        val id = data[position].id
        imageViewHolder.binding.imageView.transitionName = id
        Glide.with(context)
            .load(Uri.fromFile(File("${context.filesDir.absoluteFile}/$id$IMAGE_EXTENSION")))
            .placeholder(R.drawable.ic_baseline_image_24)
            .into(imageViewHolder.binding.imageView)
        imageViewHolder.itemView.setOnClickListener { view ->
            val bundle = bundleOf("id" to id)
            val extras = FragmentNavigatorExtras(imageViewHolder.binding.imageView to id)
            view.findNavController().navigate(R.id.action_homeFragment_to_detailsFragment, bundle, null, extras)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun addData(newData: List<Result>) {
        val lastPosition = data.size
        newData.forEach {
            data.add(it)
        }
        notifyItemInserted(lastPosition)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }
}