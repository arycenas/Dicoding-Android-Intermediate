package com.intermediate.storyapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intermediate.storyapp.data.response.ListStoryItem
import com.intermediate.storyapp.databinding.StoriesListBinding

class StoryAdapter(private val onItemClickListener: OnItemClickListener) :
    PagingDataAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
    class MyViewHolder(
        private val onItemClickListener: OnItemClickListener, val binding: StoriesListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            binding.itemTitle.text = "${data.name}"
            binding.itemDescription.text = data.description

            Glide.with(binding.root.context).load(data.photoUrl).into(binding.imgItemPhoto)

            binding.root.setOnClickListener {
                val component = listOf(
                    Pair(
                        binding.imgItemPhoto, "photo"
                    ), Pair(
                        binding.itemTitle, "title"
                    ), Pair(
                        binding.itemDescription, "description"
                    )
                )
                onItemClickListener.onItemClick(data, component)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val storiesListBinding = StoriesListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(onItemClickListener, storiesListBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val listStoryItem = getItem(position)
        if (listStoryItem != null) {
            holder.bind(listStoryItem)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: ListStoryItem, component: List<Pair<View, String>>)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem, newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}