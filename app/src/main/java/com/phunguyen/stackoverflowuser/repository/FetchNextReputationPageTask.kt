package com.phunguyen.stackoverflowuser.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phunguyen.stackoverflowuser.api.*
import com.phunguyen.stackoverflowuser.db.AppDatabase
import com.phunguyen.stackoverflowuser.utils.PAGE_SIZE
import com.phunguyen.stackoverflowuser.valueobject.Resource
import timber.log.Timber
import java.io.IOException

/**
 * A task that reads the search result in the database and fetches the next page, if it has one.
 */
class FetchNextReputationPageTask constructor(
    private val userID: String,
    private val sofService: SOFService,
    private val database: AppDatabase
) : Runnable {
    private val _liveData = MutableLiveData<Resource<Boolean>>()
    val liveData: LiveData<Resource<Boolean>> = _liveData

    override fun run() {
        val totalItem = database.reputationDao().getCount()
        val newValue = try {
            val response = sofService.getReputationsCallable(userID,(totalItem / PAGE_SIZE) + 1).execute()
            when (val apiResponse = ApiResponse.create(response)) {
                is ApiSuccessResponse -> {
                    database.runInTransaction {
                        Timber.d("body.items: ${apiResponse.body.items.size}")
                        database.reputationDao().insert(apiResponse.body.items)
                    }
                    Timber.d("apiResponse.body.hasMore: ${apiResponse.body.hasMore}")
                    Resource.success(apiResponse.body.hasMore)
                }
                is ApiEmptyResponse -> Resource.success(false)
                is ApiErrorResponse -> Resource.error(apiResponse.errorMessage, true)
            }

        } catch (e: IOException) {
            Resource.error(e.message!!, true)
        }
        _liveData.postValue(newValue)
    }
}
