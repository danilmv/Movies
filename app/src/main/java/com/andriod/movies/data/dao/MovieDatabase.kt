package com.andriod.movies.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.andriod.movies.entity.room.ListsDto
import com.andriod.movies.entity.room.MovieDto
import com.andriod.movies.entity.room.MovieListDto

@Database(entities = [MovieDto::class, ListsDto::class, MovieListDto::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun moviesDao(): MoviesDao
}