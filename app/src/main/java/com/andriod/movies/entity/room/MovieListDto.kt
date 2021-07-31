package com.andriod.movies.entity.room

import androidx.room.Entity

@Entity(tableName = "MovieList", primaryKeys = ["movieId", "listId"] )
data class MovieListDto(
    val movieId: String,
    val listId: String,
)
