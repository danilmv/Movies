package com.andriod.movies.entity.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Videos")
data class VideoDto(
    @PrimaryKey val videoId: String,
    @ColumnInfo val name: String,
    @ColumnInfo val site: String,
    @ColumnInfo val type: String,
)
