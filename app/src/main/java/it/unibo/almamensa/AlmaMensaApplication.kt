package it.unibo.almamensa

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.osmdroid.config.Configuration

class AlmaMensaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AlmaMensaApplication)
            modules(appModule)
        }

        // Configuration for OpenStreetMap
        Configuration.getInstance().load(
            this,
            this.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }
}
