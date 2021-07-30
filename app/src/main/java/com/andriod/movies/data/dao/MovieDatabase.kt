package com.andriod.movies.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.andriod.movies.entity.room.MovieDto

@Database(entities = [MovieDto::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun moviesDao(): MoviesDao
}