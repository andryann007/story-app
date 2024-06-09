package com.andryan.storyapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.getString
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andryan.storyapp.R
import com.andryan.storyapp.data.local.entity.LocalStoryItem
import com.andryan.storyapp.databinding.ContainerStoryItemBinding
import com.andryan.storyapp.ui.activity.detail.DetailActivity
import com.squareup.picasso.Picasso

@ExperimentalPagingApi
class StoryListAdapter :
    PagingDataAdapter<LocalStoryItem, StoryListAdapter.StoryListHolder>(DiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoryListHolder {
        val binding =
            ContainerStoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryListHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryListHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(holder.itemView.context, story)
        }
    }

    class StoryListHolder(private val binding: ContainerStoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, story: LocalStoryItem) {
            binding.apply {
                Picasso.get().load(story.photoUrl).noFade().into(ivItemPhoto)
                tvItemName.text = story.name
                tvItemDate.text = story.createdAt.withDateFormat()
                tvItemDescription.text = story.description

                root.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(civItemProfile, "profileImage"),
                            Pair(ivItemPhoto, "storyImage"),
                            Pair(tvItemName, "name"),
                            Pair(tvItemDate, "date"),
                            Pair(tvItemDescription, "description")
                        )

                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra(getString(root.context, R.string.extra_id), story.id)

                    context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<LocalStoryItem>() {
            override fun areItemsTheSame(
                oldItem: LocalStoryItem,
                newItem: LocalStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: LocalStoryItem,
                newItem: LocalStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}