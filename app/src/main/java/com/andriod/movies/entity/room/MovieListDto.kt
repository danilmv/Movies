package com.andriod.movies.entity.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MovieList")
data class MovieListDto(
    @PrimaryKey val movieId: String,
    @PrimaryKey val listId: String,
)
