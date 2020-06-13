package com.phunguyen.stackoverflowuser.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.phunguyen.stackoverflowuser.api.CommonListResponse
import com.phunguyen.stackoverflowuser.api.SOFService
import com.phunguyen.stackoverflowuser.db.AppDatabase
import com.phunguyen.stackoverflowuser.db.UserDao
import com.phunguyen.stackoverflowuser.util.ApiUtil.successCall
import com.phunguyen.stackoverflowuser.util.InstantAppExecutors
import com.phunguyen.stackoverflowuser.util.TestUtil
import com.phunguyen.stackoverflowuser.util.mock
import com.phunguyen.stackoverflowuser.valueobject.Resource
import com.phunguyen.stackoverflowuser.valueobject.User
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
class UserRepositoryTest {

    private lateinit var repository: UserRepository
    private val service = mock(SOFService::class.java)
    private val userDao = mock(UserDao::class.java)

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        val database = mock(AppDatabase::class.java)
        `when`(database.userDao()).thenReturn(userDao)
        repository = UserRepository(InstantAppExecutors(), database, service)
    }

    @Test
    fun loadUsers() {
        repository.loadUsers(false)
        verify(userDao).getUsers()
    }

    @Test
    fun loadUsersBookmark() {
        repository.loadUsers(true)
        verify(userDao).getUsersBookmark(true)
    }

    @Test
    fun goToNetwork() {
        val dbData = MutableLiveData<List<User>>()
        `when`(userDao.getUsers()).thenReturn(dbData)
        val users = TestUtil.usersDummy
        val call = successCall(users)
        `when`(service.getUsers(1)).thenReturn(call)
        val observer = mock<Observer<Resource<List<User>>>>()

        repository.loadUsers(false).observeForever(observer)
        verify(service, never()).getUsers(1)
        val updatedDbData = MutableLiveData<List<User>>()
        `when`(userDao.getUsers()).thenReturn(updatedDbData)
        dbData.value = null
        verify(service).getUsers(1)
    }

    @Test
    fun notGoToNetwork() {
        val dbData = MutableLiveData<List<User>>()
        val users = TestUtil.usersDummy
        dbData.value = users.items
        `when`(userDao.getUsers()).thenReturn(dbData)
        val observer = mock<Observer<Resource<List<User>>>>()

        repository.loadUsers(false).observeForever(observer)
        verify(service, never()).getUsers(1)
        verify(observer).onChanged(Resource.success(dbData.value))
    }

    @Test
    fun loadUsersMore() {
        `when`(userDao.getCount()).thenReturn(30)
        val users = TestUtil.usersDummy
        val call = mock<Call<CommonListResponse<User>>>()
        `when`(call.execute()).thenReturn(Response.success(users))
        `when`(service.getUsersCallable(2)).thenReturn(call)

        val observer = mock<Observer<Resource<Boolean>>>()
        repository.loadMoreUsers().observeForever(observer)
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