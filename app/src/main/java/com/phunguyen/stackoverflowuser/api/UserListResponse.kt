package com.phunguyen.stackoverflowuser.api

import com.google.gson.annotations.SerializedName

data class CommonListResponse<T>(
    @SerializedName("has_more")
    val hasMore: Boolean,
    @SerializedName("items")
    val items: List<T>
)