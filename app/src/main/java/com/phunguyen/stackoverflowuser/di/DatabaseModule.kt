package com.phunguyen.stackoverflowuser.di

import android.app.Application
import androidx.room.Room
import com.phunguyen.stackoverflowuser.db.AppDatabase
import com.phunguyen.stackoverflowuser.db.ReputationDao
import com.phunguyen.stackoverflowuser.db.UserDao
import com.phunguyen.stackoverflowuser.utils.DATABASE_NAME
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDb(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME).build()
    }

    @Singleton
    @Provides
    fun provideUserDao(appDb: AppDatabase): UserDao {
        return appDb.userDao()
    }

    @Singleton
    @Provides
    fun provideReputationDao(appDb: AppDatabase): ReputationDao {
        return appDb.reputationDao()
    }

}