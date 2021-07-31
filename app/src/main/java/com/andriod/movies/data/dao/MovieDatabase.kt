package com.andriod.movies.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.andriod.movies.entity.room.*

@Database(entities = [MovieDto::class, ListsDto::class, MovieListDto::class, GenreDto::class, MovieGenreDto::class],
    version = 1)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun moviesDao(): MoviesDao
}