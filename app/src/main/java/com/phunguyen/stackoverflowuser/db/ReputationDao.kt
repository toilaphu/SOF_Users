package com.phunguyen.stackoverflowuser.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phunguyen.stackoverflowuser.valueobject.Reputation

/**
 * Interface for database access for Reputation related operations.
 */
@Dao
interface ReputationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reputation: Reputation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: List<Reputation>)

    @Query("SELECT * FROM sof_reputation WHERE userID = :userID ORDER BY createdAt Desc")
    fun getReputations(userID: String): LiveData<List<Reputation>>

    @Query("SELECT COUNT(*) FROM sof_reputation WHERE userID = :userID")
    fun getCount(userID: String): Int
}
