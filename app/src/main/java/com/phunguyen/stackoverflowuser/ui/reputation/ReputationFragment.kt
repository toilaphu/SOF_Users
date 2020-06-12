package com.phunguyen.stackoverflowuser.ui.reputation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.phunguyen.stackoverflowuser.R
import com.phunguyen.stackoverflowuser.databinding.FragmentReputationBinding
import com.phunguyen.stackoverflowuser.di.Injectable
import com.phunguyen.stackoverflowuser.ui.adapter.ReputationAdapter
import com.phunguyen.stackoverflowuser.utils.AppUtils
import com.phunguyen.stackoverflowuser.utils.autoCleared
import com.phunguyen.stackoverflowuser.utils.toFormattedDate
import com.phunguyen.stackoverflowuser.valueobject.Status
import timber.log.Timber
import javax.inject.Inject

class ReputationFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val args: ReputationFragmentArgs by navArgs()
    private val reputationViewModel: ReputationViewModel by viewModels {
        viewModelFactory
    }
    private var binding by autoCleared<FragmentReputationBinding>()
    private var adapter by autoCleared<ReputationAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReputationBinding.inflate(inflater, container, false)
        context ?: return binding.root
        reputationViewModel.userID = args.userID
        initUI()
        return binding.root
    }

    private fun initUI() {
        adapter = ReputationAdapter()
        binding.reputationListRv.adapter = adapter
        binding.reputationListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0 || binding.loadingMore) return
                (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    .takeIf {
                        it == adapter.itemCount - 1
                    }?.let {
                        reputationViewModel.loadMoreReputations()
                    }
            }
        })
        binding.userLayout.bookmarkIcon.setOnClickListener {
            AppUtils.disableViewInDuration(it, 1_000)
            binding.userLayout.userItem?.let { user -> reputationViewModel.updateUserBookmark(user) }
        }
        reputationViewModel.getUser().observe(viewLifecycleOwner, Observer {
            binding.userLayout.userName.text = it.userName
            binding.userLayout.userName.text = it.userName
            binding.userLayout.lastAccessDate.text = resources.getString(
                R.string.user_last_access_date, it.lastAccessDate.toFormattedDate()
            )
            binding.userLayout.userReputation.text = resources.getString(
                R.string.user_reputation,
                it.reputation.toString()
            )
            binding.userLayout.userLocation.text =
                if (it.location.isNullOrEmpty()) "Unknown" else it.location
            Glide.with(this)
                .asBitmap()
                .load(it.userAvatar)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .into(BitmapImageViewTarget(binding.userLayout.userAvatar))
            binding.userLayout.bookmarkIcon.setImageResource(if (it.isBookmarked) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)
            binding.userLayout.userItem = it
        })
        reputationViewModel.getReputations().observe(viewLifecycleOwner, Observer {
            binding.progressBar.visibility =
                if (it.status == Status.LOADING) View.VISIBLE else View.GONE
            if (it.status == Status.SUCCESS) {
                adapter.submitList(it.data)
            }
        })
        reputationViewModel.loadMoreStatus.observe(viewLifecycleOwner, Observer { loadingMore ->
            if (loadingMore == null) {
                binding.loadingMore = false
            } else {
                binding.loadingMore = loadingMore.isRunning
                val error = loadingMore.errorMessageIfNotHandled
                if (error != null) {
                    Timber.e(error)
                }
            }
        })
    }
}