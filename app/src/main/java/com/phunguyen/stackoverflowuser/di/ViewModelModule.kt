package com.phunguyen.stackoverflowuser.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phunguyen.stackoverflowuser.ui.common.SharedViewModel
import com.phunguyen.stackoverflowuser.ui.reputation.ReputationViewModel
import com.phunguyen.stackoverflowuser.ui.user.UsersViewModel
import com.phunguyen.stackoverflowuser.valueobject.ViewModelFactory

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(UsersViewModel::class)
    abstract fun bindUserViewModel(userViewModel: UsersViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReputationViewModel::class)
    abstract fun bindReputationViewModel(reputationViewModel: ReputationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SharedViewModel::class)
    abstract fun bindSharedViewModel(sharedViewModel: SharedViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
