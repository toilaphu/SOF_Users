package com.phunguyen.stackoverflowuser.ui.reputation

import androidx.lifecycle.*
import com.phunguyen.stackoverflowuser.repository.ReputationRepository
import com.phunguyen.stackoverflowuser.testing.OpenForTesting
import com.phunguyen.stackoverflowuser.valueobject.Resource
import com.phunguyen.stackoverflowuser.valueobject.Status
import com.phunguyen.stackoverflowuser.valueobject.User
import kotlinx.coroutines.launch
import javax.inject.Inject

@OpenForTesting
class ReputationViewModel @Inject constructor(private val repository: ReputationRepository) :
    ViewModel() {

    private val nextPageHandler = NextPageHandler(repository)
    val loadMoreStatus: LiveData<LoadMoreState>
        get() = nextPageHandler.loadMoreState

    lateinit var userID: String

    fun getUser() = repository.getUser(userID)

    fun getReputations() = repository.loadReputations(userID)

    fun loadMoreReputations() {
        nextPageHandler.loadMoreReputations(userID)
    }

    fun updateUserBookmark(user: User) {
        viewModelScope.launch {
            repository.updateBookmark(user)
        }
    }

    class NextPageHandler(private val repository: ReputationRepository) :
        Observer<Resource<Boolean>> {
        private var nextPageLiveData: LiveData<Resource<Boolean>>? = null
        val loadMoreState = MutableLiveData<LoadMoreState>()

        init {
            reset()
        }

        fun loadMoreReputations(userID: String) {
            nextPageLiveData = repository.loadMoreReputations(userID)
            loadMoreState.value = LoadMoreState(isRunning = true, errorMessage = null)
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
                            LoadMoreState(isRunning = false, errorMessage = null)
                        )
                    }
                    Status.ERROR -> {
                        unregister()
                        loadMoreState.setValue(
                            LoadMoreState(isRunning = false, errorMessage = result.message)
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

        fun reset() {
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