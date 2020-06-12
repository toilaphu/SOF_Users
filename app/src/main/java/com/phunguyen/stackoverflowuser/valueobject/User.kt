package com.phunguyen.stackoverflowuser.valueobject

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "sof_user")
data class User(
    @field:SerializedName("user_id")
    @PrimaryKey
    val userID: Int,
    @field:SerializedName("display_name")
    var userName: String,
    @field:SerializedName("profile_image")
    val userAvatar: String,
    @field:SerializedName("reputation")
    val reputation: Long,
    @field:SerializedName("location")
    val location: String?,
    @field:SerializedName("last_access_date")
    val lastAccessDate: Long,
    var isBookmarked: Boolean
) {
    override fun equals(other: Any?) = (other is User)
            && isBookmarked == other.isBookmarked
            && userID == other.userID
            && lastAccessDate == other.lastAccessDate
            && userAvatar == other.userAvatar

    override fun hashCode(): Int {
        var result = userID
        result = 31 * result + userName.hashCode()
        result = 31 * result + userAvatar.hashCode()
        result = 31 * result + reputation.hashCode()
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + lastAccessDate.hashCode()
        result = 31 * result + isBookmarked.hashCode()
        return result
    }

}