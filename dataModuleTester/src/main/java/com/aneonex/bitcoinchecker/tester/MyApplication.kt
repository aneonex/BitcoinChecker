package com.aneonex.bitcoinchecker.tester

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant


@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initializeLogging()

        Timber.i("*** Application started ***")
    }

    companion object {
        private fun initializeLogging(){
            if (BuildConfig.DEBUG) {
                plant(DebugTree())
            }
        }
    }
}