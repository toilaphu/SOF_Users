package com.phunguyen.stackoverflowuser.repository

import androidx.lifecycle.LiveData
import com.phunguyen.stackoverflowuser.AppExecutors
import com.phunguyen.stackoverflowuser.api.ApiResponse
import com.phunguyen.stackoverflowuser.api.CommonListResponse
import com.phunguyen.stackoverflowuser.api.SOFService
import com.phunguyen.stackoverflowuser.db.AppDatabase
import com.phunguyen.stackoverflowuser.db.ReputationDao
import com.phunguyen.stackoverflowuser.valueobject.Reputation
import com.phunguyen.stackoverflowuser.valueobject.Resource
import com.phunguyen.stackoverflowuser.valueobject.User
import javax.inject.Inject

class ReputationRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val database: AppDatabase,
    private val reputationDao: ReputationDao,
    private val sofService: SOFService
) {

    fun getUser(userID: String) = database.userDao().getUser(userID)

    suspend fun updateBookmark(user: User): User? {
        user.isBookmarked = !user.isBookmarked
        if (database.userDao().updateUser(user) == 1) {
            return user
        }
        return null
    }

    fun loadReputations(userID: String): LiveData<Resource<List<Reputation>>> {
        return object :
            NetworkBoundResource<List<Reputation>, CommonListResponse<Reputation>>(appExecutors) {
            override fun saveCallResult(item: CommonListResponse<Reputation>) {
                item.items.takeIf { it.isNotEmpty() }?.let {
                    reputationDao.insert(it)
                }
            }

            override fun shouldFetch(data: List<Reputation>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Reputation>> {
                return reputationDao.getReputations()
            }

            override fun createCall(): LiveData<ApiResponse<CommonListResponse<Reputation>>> {
                return sofService.getReputations(userID, 1)
            }

        }.asLiveData()
    }

    fun loadMoreReputations(userID: String): LiveData<Resource<Boolean>> {
        val fetchNextReputationPageTask = FetchNextReputationPageTask(userID, sofService, database)
        appExecutors.networkIO().execute(fetchNextReputationPageTask)
        return fetchNextReputationPageTask.liveData
    }
}