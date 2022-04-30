package com.andriod.movies.entity.room

import androidx.room.Entity

@Entity(tableName = "MovieVideo", primaryKeys = ["movieId", "videoId", "lang"] )
data class MovieVideoDto(
    val movieId: String,
    val videoId: String,
    val lang: String,
)
