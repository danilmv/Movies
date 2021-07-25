package com.andriod.movies.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.andriod.movies.MyViewModel

class MovieDataDownloadService: Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        Log.d(TAG, "onCreate() called")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,
            "onStartCommand() called with: intent = $intent, flags = $flags, startId = $startId")
        MyViewModel.getMoreData()
        return super.onStartCommand(intent, flags, startId)
    }

    companion object{
        const val TAG = "@@MovieDataService"
    }
}