package com.andriod.movies.data.dao

import androidx.room.*
import com.andriod.movies.entity.room.MovieDto

@Dao
interface MoviesDao {
    @Query("SELECT * FROM movies")
    fun getAll(): List<MovieDto>

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovie(id: String): MovieDto

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovie(movieDto: MovieDto)

    @Update
    fun updateMovie(movieDto: MovieDto)

    @Delete
    fun deleteDto(movieDto: MovieDto)
}