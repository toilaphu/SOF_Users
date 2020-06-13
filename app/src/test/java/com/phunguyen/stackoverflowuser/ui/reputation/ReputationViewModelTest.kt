package com.phunguyen.stackoverflowuser.ui.reputation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.phunguyen.stackoverflowuser.repository.ReputationRepository
import com.phunguyen.stackoverflowuser.util.TestUtil
import com.phunguyen.stackoverflowuser.util.mock
import com.phunguyen.stackoverflowuser.valueobject.Reputation
import com.phunguyen.stackoverflowuser.valueobject.Resource
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class ReputationViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = mock(ReputationRepository::class.java)
    private lateinit var reputationViewModel: ReputationViewModel

    @Before
    fun init() {
        // need to init after instant executor rule is established.
        reputationViewModel = ReputationViewModel(repository)
        reputationViewModel.userID = ""
    }

    @Test
    fun loadReputations() {
        val dbData = MutableLiveData<Resource<List<Reputation>>>()
        dbData.value = Resource.success(TestUtil.reputationsDummy.items)
        `when`(repository.loadReputations("")).thenReturn(dbData)
        val observer = mock<Observer<Resource<List<Reputation>>>>()
        reputationViewModel.getReputations().observeForever(observer)
        verify(observer).onChanged(dbData.value)
    }

}