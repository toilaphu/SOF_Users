package com.phunguyen.stackoverflowuser.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.phunguyen.stackoverflowuser.valueobject.User

/**
 * Interface for database access for User related operations.
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: List<User>)

    @Query("SELECT * FROM sof_user ORDER BY reputation Desc")
    fun getUsers(): LiveData<List<User>>

    @Query("SELECT * FROM sof_user WHERE isBookmarked = :isBookmarked ORDER BY reputation Desc")
    fun getUsersBookmark(isBookmarked: Boolean): LiveData<List<User>>

    @Query("SELECT * from sof_user WHERE userID = :userID")
    fun getUser(userID: String): LiveData<User>

    @Query("SELECT COUNT(*) FROM sof_user")
    fun getCount(): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(user: User): Int
}
