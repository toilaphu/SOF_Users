package com.phunguyen.stackoverflowuser.repository

import androidx.lifecycle.LiveData
import com.phunguyen.stackoverflowuser.AppExecutors
import com.phunguyen.stackoverflowuser.api.CommonListResponse
import com.phunguyen.stackoverflowuser.api.SOFService
import com.phunguyen.stackoverflowuser.db.AppDatabase
import com.phunguyen.stackoverflowuser.db.UserDao
import com.phunguyen.stackoverflowuser.testing.OpenForTesting
import com.phunguyen.stackoverflowuser.valueobject.Resource
import com.phunguyen.stackoverflowuser.valueobject.User
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
class UserRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val database: AppDatabase,
    private val sofService: SOFService
) {
    fun loadUsers(isBookmarkOnly: Boolean): LiveData<Resource<List<User>>> {
        return object : NetworkBoundResource<List<User>, CommonListResponse<User>>(appExecutors) {
            override fun saveCallResult(item: CommonListResponse<User>) {
                item.items.takeIf { it.isNotEmpty() }?.let {
                    database.userDao().insert(it)
                }
            }

            override fun shouldFetch(data: List<User>?) = (data == null || data.isEmpty())

            override fun loadFromDb() =
                if (isBookmarkOnly) database.userDao().getUsersBookmark(isBookmarkOnly) else database.userDao().getUsers()

            override fun createCall() = sofService.getUsers(1)

        }.asLiveData()
    }

    fun loadMoreUsers(): LiveData<Resource<Boolean>> {
        val fetchNextUsersPageTask = FetchNextUsersPageTask(sofService, database)
        appExecutors.networkIO().execute(fetchNextUsersPageTask)
        return fetchNextUsersPageTask.liveData
    }

    suspend fun updateBookmark(user: User): User? {
        user.isBookmarked = !user.isBookmarked
        if (database.userDao().updateUser(user) == 1) {
            return user
        }
        return null
    }
}