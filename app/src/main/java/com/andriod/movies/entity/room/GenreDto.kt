package com.andriod.movies.entity.room

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "Genres", primaryKeys = ["genreId", "lang"])
data class GenreDto(
    val genreId: String,
    val lang: String,
    @ColumnInfo val text: String
)
