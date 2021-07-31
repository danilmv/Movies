package com.andriod.movies.entity.room

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "Lists", primaryKeys = ["id", "lang"])
data class ListsDto(
    val id: String,
    val lang: String,
    @ColumnInfo val text: String,
)
