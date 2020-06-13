package com.phunguyen.stackoverflowuser.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.phunguyen.stackoverflowuser.api.CommonListResponse
import com.phunguyen.stackoverflowuser.api.SOFService
import com.phunguyen.stackoverflowuser.db.AppDatabase
import com.phunguyen.stackoverflowuser.db.ReputationDao
import com.phunguyen.stackoverflowuser.db.UserDao
import com.phunguyen.stackoverflowuser.util.ApiUtil.successCall
import com.phunguyen.stackoverflowuser.util.InstantAppExecutors
import com.phunguyen.stackoverflowuser.util.TestUtil
import com.phunguyen.stackoverflowuser.util.mock
import com.phunguyen.stackoverflowuser.valueobject.Reputation
import com.phunguyen.stackoverflowuser.valueobject.Resource
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import retrofit2.Call
import retrofit2.Response

@RunWith(JUnit4::class)
class ReputationRepositoryTest {

    private lateinit var repository: ReputationRepository
    private val service = mock(SOFService::class.java)
    private val reputationDao = mock(ReputationDao::class.java)
    private val userDao = mock(UserDao::class.java)

    private val userID = "1144035"

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        val database = mock(AppDatabase::class.java)
        `when`(database.reputationDao()).thenReturn(reputationDao)
        `when`(database.userDao()).thenReturn(userDao)
        repository = ReputationRepository(InstantAppExecutors(), database, reputationDao, service)
    }

    @Test
    fun loadReputations() {
        repository.loadReputations("1144035")
        verify(reputationDao).getReputations("1144035")
    }

    @Test
    fun goToNetwork() {
        val dbData = MutableLiveData<List<Reputation>>()
        `when`(reputationDao.getReputations(userID)).thenReturn(dbData)
        val reputations = TestUtil.reputationsDummy
        val call = successCall(reputations)
        `when`(service.getReputations(userID, 1)).thenReturn(call)
        val observer = mock<Observer<Resource<List<Reputation>>>>()

        repository.loadReputations(userID).observeForever(observer)
        verify(service, never()).getReputations(userID, 1)
        val updatedDbData = MutableLiveData<List<Reputation>>()
        `when`(reputationDao.getReputations(userID)).thenReturn(updatedDbData)
        dbData.value = null
        verify(service).getReputations(userID, 1)
    }

    @Test
    fun notGoToNetwork() {
        val dbData = MutableLiveData<List<Reputation>>()
        val reputations = TestUtil.reputationsDummy
        dbData.value = reputations.items
        `when`(reputationDao.getReputations(userID)).thenReturn(dbData)
        val observer = mock<Observer<Resource<List<Reputation>>>>()

        repository.loadReputations(userID).observeForever(observer)
        verify(service, never()).getReputations(userID, 1)
        verify(observer).onChanged(Resource.success(dbData.value))
    }

    @Test
    fun loadReputationsMore() {
        `when`(reputationDao.getCount(userID)).thenReturn(30)
        val reputations = TestUtil.reputationsDummy
        val call = mock<Call<CommonListResponse<Reputation>>>()
        `when`(call.execute()).thenReturn(Response.success(reputations))
        `when`(service.getReputationsCallable(userID, 2)).thenReturn(call)

        val observer = mock<Observer<Resource<Boolean>>>()
        repository.loadMoreReputations(userID).observeForever(observer)
        verify(observer).onChanged(Resource.success(true))
    }

    @Test
    fun updateBookmark() = runBlocking {
        val user = TestUtil.userDummy
        `when`(userDao.updateUser(user)).thenReturn(1)

        val userUpdate = repository.updateBookmark(user)
        assertThat(userUpdate?.isBookmarked).isTrue()
    }

    @Test
    fun updateBookmark_Fail() = runBlocking {
        val user = TestUtil.userDummy
        `when`(userDao.updateUser(user)).thenReturn(0)

        val userUpdate = repository.updateBookmark(user)
        assertThat(userUpdate).isNull()
    }

}