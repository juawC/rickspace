package com.app.juawcevada.rickspace.util

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.app.juawcevada.rickspace.TestApp

class CustomAppTestRunner: AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, TestApp::class.java.name, context)
    }
}