package com.phunguyen.stackoverflowuser.di

import com.phunguyen.stackoverflowuser.MainActivity
import com.phunguyen.stackoverflowuser.ui.reputation.ReputationFragment
import com.phunguyen.stackoverflowuser.ui.user.UsersFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AppModule {

    @ContributesAndroidInjector
    abstract fun mainActivityInjector(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeUsersFragment(): UsersFragment

    @ContributesAndroidInjector
    abstract fun contributeReputationFragment(): ReputationFragment

}