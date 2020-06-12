package com.phunguyen.stackoverflowuser.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.phunguyen.stackoverflowuser.R
import com.phunguyen.stackoverflowuser.databinding.UserItemBinding
import com.phunguyen.stackoverflowuser.ui.user.UsersFragmentDirections
import com.phunguyen.stackoverflowuser.utils.AppUtils
import com.phunguyen.stackoverflowuser.utils.toFormattedDate
import com.phunguyen.stackoverflowuser.valueobject.User
import java.util.concurrent.Executors

class UserAdapter(private val bookmarkClickCallback: ((User) -> Unit)?) :
    ListAdapter<User, RecyclerView.ViewHolder>(
        AsyncDifferConfig.Builder<User>(DIFF_CALLBACK)
            .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
            .build()
    ) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User) =
                oldItem.userID == newItem.userID

            override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val userItemBinding =
            UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        userItemBinding.viewContainer.setOnClickListener {
            userItemBinding.userItem?.userID?.let { userID ->
                val direction =
                    UsersFragmentDirections.actionUsersFragmentToReputationFragment(userID.toString())
                it.findNavController().navigate(direction)
            }
        }
        userItemBinding.bookmarkIcon.setOnClickListener {
            AppUtils.disableViewInDuration(it, 1_000)
            userItemBinding.userItem?.let { userItem ->
                bookmarkClickCallback?.invoke(userItem)
            }
        }
        return UserItemViewHolder(userItemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            (holder as UserItemViewHolder).bind(holder, it)
        }
    }

    class UserItemViewHolder(private val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(holder: RecyclerView.ViewHolder, item: User) {
            binding.apply {
                userName.text = item.userName
                lastAccessDate.text = holder.itemView.resources.getString(
                    R.string.user_last_access_date, item.lastAccessDate.toFormattedDate()
                )
                userReputation.text = holder.itemView.resources.getString(
                    R.string.user_reputation,
                    item.reputation.toString()
                )
                userLocation.text = if (item.location.isNullOrEmpty()) "Unknown" else item.location
                Glide.with(holder.itemView)
                    .asBitmap()
                    .load(item.userAvatar)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                    .into(BitmapImageViewTarget(userAvatar))
                bookmarkIcon.setImageResource(if (item.isBookmarked) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
                userItem = item
                executePendingBindings()
            }
        }
    }
}