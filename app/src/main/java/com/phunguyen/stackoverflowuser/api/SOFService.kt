package com.phunguyen.stackoverflowuser.api

import androidx.lifecycle.LiveData
import com.phunguyen.stackoverflowuser.utils.BASE_SITE
import com.phunguyen.stackoverflowuser.utils.PAGE_SIZE
import com.phunguyen.stackoverflowuser.valueobject.Reputation
import com.phunguyen.stackoverflowuser.valueobject.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SOFService {
    @GET("/users")
    fun getUsers(@Query("page") page: Int, @Query("pagesize") pageSize: Int = PAGE_SIZE, @Query("site") site: String = BASE_SITE): LiveData<ApiResponse<CommonListResponse<User>>>

    @GET("/users")
    fun getUsersCallable(
        @Query("page") page: Int, @Query("pagesize") pageSize: Int = PAGE_SIZE, @Query(
            "site"
        ) site: String = BASE_SITE
    ): Call<CommonListResponse<User>>

    @GET("users/{userID}/reputation-history")
    fun getReputations(
        @Path("userID") userID: String, @Query("page") page: Int, @Query("pagesize") pageSize: Int = PAGE_SIZE,
        @Query("site") site: String = BASE_SITE
    ): LiveData<ApiResponse<CommonListResponse<Reputation>>>

    @GET("users/{userID}/reputation-history")
    fun getReputationsCallable(
        @Path("userID") userID: String, @Query("page") page: Int, @Query("pagesize") pageSize: Int = PAGE_SIZE,
        @Query("site") site: String = BASE_SITE
    ): Call<CommonListResponse<Reputation>>
}