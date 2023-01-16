package com.ecemsevvalcinar.runningtracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


// dagger is compile time injection
@HiltAndroidApp
class BaseApplication: Application()  {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}