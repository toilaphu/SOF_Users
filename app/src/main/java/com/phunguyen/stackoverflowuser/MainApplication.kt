package com.phunguyen.stackoverflowuser

import android.app.Application
import com.phunguyen.stackoverflowuser.di.AppInjector
import com.phunguyen.stackoverflowuser.utils.SharedData
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject

class MainApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
        if (BuildConfig.DEBUG) {
            // Setup Log configuration:
            Timber.plant(Timber.DebugTree())
        }
        SharedData.init(applicationContext)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

}