package com.example.androidsideproject

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.androidsideproject.DI.AppContainer
import com.example.androidsideproject.DI.DefaultAppContainer
import com.google.android.material.snackbar.Snackbar

class MainApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
    }
}