package com.andriod.movies

import android.app.Application

class MovieApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        MyViewModel.initData()
    }
}