package com.phunguyen.stackoverflowuser.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.phunguyen.stackoverflowuser.util.getOrAwaitValue
import com.phunguyen.stackoverflowuser.utils.LiveDataCallAdapterFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class SOFServiceTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: SOFService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(SOFService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun getUsers() {
        enqueueResponse("sof_users.json")
        val userList = (service.getUsers(1).getOrAwaitValue() as ApiSuccessResponse).body

        val request = mockWebServer.takeRequest()
        Truth.assertThat(request.requestUrl.toString())
            .contains("users?page=1&pagesize=30&site=stackoverflow")
        Truth.assertThat(userList.items).isNotNull()
        Truth.assertThat(userList.items.size).isEqualTo(30)
    }

    @Test
    fun getReputation() {
        enqueueResponse("sof_reputations.json")
        val userList =
            (service.getReputations("1144035", 1).getOrAwaitValue() as ApiSuccessResponse).body

        val request = mockWebServer.takeRequest()
        Truth.assertThat(request.path)
            .isEqualTo("/users/1144035/reputation-history?page=1&pagesize=30&site=stackoverflow")
        Truth.assertThat(userList.items).isNotNull()
        Truth.assertThat(userList.items.size).isEqualTo(30)
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader!!
            .getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }
}
