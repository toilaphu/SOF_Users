package com.phunguyen.stackoverflowuser.valueobject

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "sof_reputation")
data class Reputation(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @field:SerializedName("post_id")
    val postID: Int?,
    @field:SerializedName("user_id")
    val userID: Int,
    @field:SerializedName("reputation_history_type")
    val type: String,
    @field:SerializedName("reputation_change")
    val change: String,
    @field:SerializedName("creation_date")
    val createdAt: Long
) {
    override fun equals(other: Any?) =
        (other is Reputation) && postID == other.postID && type == other.type

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (postID ?: 0)
        result = 31 * result + userID
        result = 31 * result + type.hashCode()
        result = 31 * result + change.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }
}