package com.fit3163.myapplication

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase (runs once when app starts)
        FirebaseApp.initializeApp(this)
    }
}
