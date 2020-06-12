package com.phunguyen.stackoverflowuser.ui.user

import androidx.lifecycle.*
import com.phunguyen.stackoverflowuser.repository.UserRepository
import com.phunguyen.stackoverflowuser.ui.adapter.UserAdapter
import com.phunguyen.stackoverflowuser.valueobject.Resource
import com.phunguyen.stackoverflowuser.valueobject.Status
import com.phunguyen.stackoverflowuser.valueobject.User
import kotlinx.coroutines.launch
import javax.inject.Inject

class UsersViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    private val nextPageHandler = NextPageHandler(repository)

    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState

    private val _isReload = MutableLiveData<Boolean>()
    private var _isBookmark = false

    var userList = _isReload.switchMap {
        repository.loadUsers(_isBookmark)
    }

    fun refresh() {
        _isReload.value = true
    }

    fun displayWithBookmarkOption(isBookmark: Boolean) {
        _isBookmark = isBookmark
        _isReload.value = true
    }

    fun loadMoreUsers() {
        nextPageHandler.loadMoreUsers()
    }

    fun updateBookmark(adapter: UserAdapter, user: User) {
        viewModelScope.launch {
            repository.updateBookmark(user)?.let { userUpdated ->
                userList.value?.let { dataResource ->
                    dataResource.data?.withIndex()?.find {
                        it.value.userID == userUpdated.userID
                    }?.apply {
                        value.isBookmarked = !value.isBookmarked
                        adapter.notifyItemChanged(index)
                    }
                }
            }
        }
    }

    class NextPageHandler(private val repository: UserRepository) : Observer<Resource<Boolean>> {
        private var nextPageLiveData: LiveData<Resource<Boolean>>? = null
        val loadMoreState = MutableLiveData<LoadMoreState>()

        init {
            reset()
        }

        fun loadMoreUsers() {
            nextPageLiveData = repository.loadMoreUsers()
            loadMoreState.value = LoadMoreState(
                isRunning = true,
                errorMessage = null
            )
            nextPageLiveData?.observeForever(this)
        }

        override fun onChanged(result: Resource<Boolean>?) {
            if (result == null) {
                reset()
            } else {
                when (result.status) {
                    Status.SUCCESS -> {
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = null
                            )
                        )
                    }
                    Status.ERROR -> {
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(
                                isRunning = false,
                                errorMessage = result.message
                            )
                        )
                    }
                    Status.LOADING -> {
                        // Ignore this time
                    }
                }
            }
        }

        private fun unregister() {
            nextPageLiveData?.removeObserver(this)
            nextPageLiveData = null
        }

        private fun reset() {
            unregister()
            loadMoreState.value = LoadMoreState(
                isRunning = false,
                errorMessage = null
            )
        }
    }

    class LoadMoreState(val isRunning: Boolean, val errorMessage: String?) {
        private var handledError = false

        val errorMessageIfNotHandled: String?
            get() {
                if (handledError) {
                    return null
                }
                handledError = true
                return errorMessage
            }

    }
}