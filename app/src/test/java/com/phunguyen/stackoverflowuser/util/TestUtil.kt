package com.phunguyen.stackoverflowuser.util

import com.phunguyen.stackoverflowuser.api.CommonListResponse
import com.phunguyen.stackoverflowuser.valueobject.Reputation
import com.phunguyen.stackoverflowuser.valueobject.User

object TestUtil {

    val userDummy = createUser(1, "ABC", "avatarURL", 10, 1591983430, false)

    val usersDummy = createUsers(30, 1, "ABC", "avatarURL", 10, 1591983430, false)

    val reputationsDummy =
        createReputations(30, 62348680, 1144035, "answer_accepted", "15", 1591981845)

    fun createUsers(
        count: Int,
        userId: Int,
        userName: String,
        userAvatar: String,
        reputation: Long,
        lastAccessDate: Long,
        isBookmarked: Boolean
    ): CommonListResponse<User> {
        return CommonListResponse(hasMore = true, items = (0 until count).map {
            User(
                userID = userId + it,
                userName = userName + it,
                userAvatar = userAvatar + it,
                reputation = reputation + it,
                lastAccessDate = lastAccessDate + it,
                location = null,
                isBookmarked = isBookmarked
            )
        })
    }

    fun createUser(
        userId: Int,
        userName: String,
        userAvatar: String,
        reputation: Long,
        lastAccessDate: Long,
        isBookmarked: Boolean
    ): User {
        return User(
            userID = userId,
            userName = userName,
            userAvatar = userAvatar,
            reputation = reputation,
            lastAccessDate = lastAccessDate,
            location = null,
            isBookmarked = isBookmarked
        )
    }

    fun createReputations(
        count: Int,
        postID: Int,
        userID: Int,
        type: String,
        change: String,
        createdAt: Long
    ): CommonListResponse<Reputation> {
        return CommonListResponse(hasMore = true, items = (0 until count).map {
            Reputation(
                id = it.toLong(),
                postID = postID + it,
                userID = userID + it,
                type = type + it,
                change = change + it,
                createdAt = createdAt + it
            )
        })
    }

}
