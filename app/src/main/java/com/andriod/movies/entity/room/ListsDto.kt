package com.andriod.movies.entity.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Lists")
data class ListsDto(
    @PrimaryKey val id: String,
    @PrimaryKey val lang: String,
    @ColumnInfo val text: String,
)
