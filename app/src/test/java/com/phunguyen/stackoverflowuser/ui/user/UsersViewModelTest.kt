package com.phunguyen.stackoverflowuser.ui.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.phunguyen.stackoverflowuser.repository.UserRepository
import com.phunguyen.stackoverflowuser.util.TestUtil
import com.phunguyen.stackoverflowuser.util.mock
import com.phunguyen.stackoverflowuser.valueobject.Resource
import com.phunguyen.stackoverflowuser.valueobject.User
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class UsersViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = mock(UserRepository::class.java)
    private lateinit var usersViewModel: UsersViewModel

    @Before
    fun init() {
        // need to init after instant executor rule is established.
        usersViewModel = UsersViewModel(repository)
    }

    @Test
    fun loadUsers() {
        val dbData = MutableLiveData<Resource<List<User>>>()
        dbData.value = Resource.success(TestUtil.usersDummy.items)
        `when`(repository.loadUsers(false)).thenReturn(dbData)
        val observer = mock<Observer<Resource<List<User>>>>()
        usersViewModel.userList.observeForever(observer)

        verifyNoMoreInteractions(repository)
        usersViewModel.refresh()
        verify(repository).loadUsers(false)
        verify(observer).onChanged(dbData.value)
    }

    @Test
    fun `Load Users with BookmarkFilter`() {
        val dbData = MutableLiveData<Resource<List<User>>>()
        dbData.value = Resource.success(TestUtil.usersDummy.items)
        `when`(repository.loadUsers(true)).thenReturn(dbData)
        val observer = mock<Observer<Resource<List<User>>>>()
        usersViewModel.userList.observeForever(observer)

        usersViewModel.userListWithBookmarkFilter(true)
        verify(repository).loadUsers(true)
    }

}