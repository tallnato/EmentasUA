package org.ementasua

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()



        // ThreeTenBP for times and dates
        AndroidThreeTen.init(this)
    }
}