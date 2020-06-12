package com.phunguyen.stackoverflowuser.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.phunguyen.stackoverflowuser.valueobject.Reputation
import com.phunguyen.stackoverflowuser.valueobject.User

@Database(entities = [User::class, Reputation::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun reputationDao(): ReputationDao
}