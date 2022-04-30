package com.andriod.movies.entity.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Movies")
data class MovieDto(
    @PrimaryKey val id: String,
    @ColumnInfo val title: String,
    @ColumnInfo(name = "original_title") val originalTitle: String,
    @ColumnInfo val runtime: String,
    @ColumnInfo(name = "released_date") val released: String,
    @ColumnInfo val year: String,
    @ColumnInfo val plot: String,
    @ColumnInfo val poster: String,
    @ColumnInfo val rating: String,
    @ColumnInfo(name = "vote_count") val votes: String,
    @ColumnInfo val revenue: String,
    @ColumnInfo val budget: String,
    @ColumnInfo(name = "media_type") val type: String,
    @ColumnInfo val isFavorite: Int,
    @ColumnInfo val isDetailed: Int,
    @ColumnInfo val background: String?,
)
