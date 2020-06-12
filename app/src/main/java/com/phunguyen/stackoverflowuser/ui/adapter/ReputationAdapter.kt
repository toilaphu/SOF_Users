package com.phunguyen.stackoverflowuser.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.phunguyen.stackoverflowuser.R
import com.phunguyen.stackoverflowuser.databinding.ReputationItemBinding
import com.phunguyen.stackoverflowuser.utils.toFormattedDate
import com.phunguyen.stackoverflowuser.valueobject.Reputation

class ReputationAdapter : ListAdapter<Reputation, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Reputation>() {
            // The ID property identifies when items are the same.
            override fun areItemsTheSame(oldItem: Reputation, newItem: Reputation) =
                (oldItem.postID == newItem.postID) && oldItem.type == newItem.type

            // If you use the "==" operator, make sure that the object implements
            // .equals(). Alternatively, write custom data comparison logic here.
            override fun areContentsTheSame(
                oldItem: Reputation, newItem: Reputation
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReputationItemViewHolder(
            ReputationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            (holder as ReputationItemViewHolder).bind(holder, it)
        }
    }

    class ReputationItemViewHolder(
        private val binding: ReputationItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(holder: RecyclerView.ViewHolder, item: Reputation) {
            val resources = holder.itemView.resources
            binding.apply {
                reputationHistoryType.text = resources.getString(
                    R.string.reputation_type, item.type
                )
                reputationChange.text = resources.getString(
                    R.string.reputation_change, item.change
                )
                postId.text = resources.getString(
                    R.string.reputation_post_id, (item.postID?:0)
                )
                creationDate.text = resources.getString(
                    R.string.reputation_created_at, item.createdAt.toFormattedDate()
                )
                executePendingBindings()
            }
        }
    }
}