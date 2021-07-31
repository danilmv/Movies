package com.andriod.movies.entity.room

import androidx.room.Entity

@Entity(tableName = "MovieGenre", primaryKeys = ["movieId", "genreId"])
data class MovieGenreDto(
    val movieId: String,
    val genreId: String,
)
