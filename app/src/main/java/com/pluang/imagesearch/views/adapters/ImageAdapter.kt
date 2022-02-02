package com.pluang.imagesearch.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pluang.imagesearch.databinding.ItemImageBinding
import com.pluang.imagesearch.databinding.ItemImageLoadingBinding
import com.pluang.imagesearch.models.Result
import com.pluang.imagesearch.models.Urls
import com.squareup.picasso.Picasso

class ImageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    private val data: ArrayList<Result> = ArrayList<Result>()
    val getCurrentDataSize get() = data.size
    var isLastPage = false


    private inner class ImageViewHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)
    private inner class LoadingViewHolder(val binding: ItemImageLoadingBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_LOADING) {
            return LoadingViewHolder(
                ItemImageLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        return ImageViewHolder(
            ItemImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                populateData(holder, position)
            }
            is LoadingViewHolder ->{
                populateData(holder,position)
            }
        }
    }

    private fun populateData(imageViewHolder:ImageViewHolder, position: Int) {
        Picasso.get()
            .load(data[position].urls.thumb)
            .into(imageViewHolder.binding.imageView)
    }

    private fun populateData(imageViewHolder:LoadingViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return  data.size
    }

    override fun getItemViewType(position: Int): Int {
        //return if (position == data.size - 1 && !isLastPage) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
        return VIEW_TYPE_ITEM
    }

    fun addData(newData: List<Result>) {
        val lastPosition = data.size
        newData.forEach {
            data.add(it)
        }
        notifyItemInserted(lastPosition)
    }
    fun clearData(){
        data.clear()
        notifyDataSetChanged()
    }
}