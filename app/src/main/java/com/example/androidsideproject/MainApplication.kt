package com.example.androidsideproject

import android.app.Application
import com.example.androidsideproject.DI.AppContainer
import com.example.androidsideproject.DI.DefaultAppContainer

class MainApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
    }
}