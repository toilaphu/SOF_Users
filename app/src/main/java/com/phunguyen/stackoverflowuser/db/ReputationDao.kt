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

    @Query("SELECT * FROM sof_reputation ORDER BY createdAt Desc")
    fun getReputations(): LiveData<List<Reputation>>

    @Query("SELECT COUNT(*) FROM sof_reputation")
    fun getCount(): Int
}
