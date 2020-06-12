package com.phunguyen.stackoverflowuser.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.phunguyen.stackoverflowuser.AppExecutors
import com.phunguyen.stackoverflowuser.databinding.FragmentUsersBinding
import com.phunguyen.stackoverflowuser.di.Injectable
import com.phunguyen.stackoverflowuser.ui.adapter.UserAdapter
import com.phunguyen.stackoverflowuser.ui.common.RetryCallback
import com.phunguyen.stackoverflowuser.ui.common.SharedViewModel
import com.phunguyen.stackoverflowuser.utils.autoCleared
import com.phunguyen.stackoverflowuser.valueobject.Status
import timber.log.Timber
import javax.inject.Inject

class UsersFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    private val userViewModel: UsersViewModel by viewModels {
        viewModelFactory
    }

    private val sharedViewModel: SharedViewModel by viewModels {
        viewModelFactory
    }

    private var binding by autoCleared<FragmentUsersBinding>()
    private var adapter by autoCleared<UserAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersBinding.inflate(inflater, container, false)
        context ?: return binding.root
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userViewModel.refresh()
        binding.lifecycleOwner = viewLifecycleOwner
        initUI()
    }

    private fun initUI() {
        adapter = UserAdapter { user ->
            userViewModel.updateBookmark(adapter, user)
        }
        binding.userListRv.adapter = adapter
        binding.userListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0 || binding.loadingMore) return
                (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    .takeIf {
                        it == adapter.itemCount - 1
                    }?.let {
                        userViewModel.loadMoreUsers()
                    }
            }
        })
        binding.userList = userViewModel.userList
        userViewModel.userList.observe(viewLifecycleOwner, Observer {
            if (it.status == Status.SUCCESS) {
                adapter.submitList(it.data)
            }
        })
        userViewModel.loadMoreStatus.observe(viewLifecycleOwner, Observer { loadingMore ->
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
        sharedViewModel.showBookmarkOnlySelected.observe(viewLifecycleOwner, Observer {
            userViewModel.displayWithBookmarkOption(it)
        })
        binding.callback = object : RetryCallback {
            override fun retry() {
                userViewModel.refresh()
            }

        }
    }
}