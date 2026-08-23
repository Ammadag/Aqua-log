package org.example.project

import android.app.Application
import com.waterdelivery.app.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class AquaLogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        initKoin {
            androidLogger()
            androidContext(this@AquaLogApplication)
        }
    }
}
