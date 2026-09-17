package com.iatv.app
import android.app.Application
import dagger.hilt.android.HiltAndroidApp
@HiltAndroidApp class IaTvApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        if(BuildConfig.DEBUG && previous != null) Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            com.iatv.app.core.diagnostics.DebugCrashReporter.record(error)
            previous.uncaughtException(thread, error)
        }
    }
}
