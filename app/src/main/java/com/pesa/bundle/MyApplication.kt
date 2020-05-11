package com.pesa.bundle

import android.app.Application
import android.widget.Button
import androidx.core.content.ContextCompat
import com.pesa.bundle.util.Constants
import timber.log.Timber
import timber.log.Timber.DebugTree


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
//        Only on Debug
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

    }


}

