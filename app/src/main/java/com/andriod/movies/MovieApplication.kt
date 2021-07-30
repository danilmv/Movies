package com.andriod.movies

import android.app.Application
import androidx.room.Room
import com.andriod.movies.data.dao.MovieDatabase

class MovieApplication : Application() {
    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            MovieDatabase::class.java,
            "MoviesDatabase.db"
        ).build()
    }

    override fun onCreate() {
        super.onCreate()

        MyViewModel.initData(database)
    }
}