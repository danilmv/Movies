package com.andriod.movies.data.dao

import androidx.room.*
import com.andriod.movies.entity.room.ListsDto
import com.andriod.movies.entity.room.MovieDto
import com.andriod.movies.entity.room.MovieListDto

@Dao
interface MoviesDao {
    @Query("SELECT * FROM movies")
    fun getAll(): List<MovieDto>

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovie(id: String): MovieDto

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovie(movieDto: MovieDto): Long

    @Update
    fun updateMovie(movieDto: MovieDto): Int

    @Delete
    fun deleteDto(movieDto: MovieDto)

    @Query("SELECT * FROM lists WHERE lang = :lang")
    fun getLists(lang: String = "EN"): List<ListsDto>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertList(vararg listDto: ListsDto)

    @Update
    fun updateList(vararg listDto: ListsDto): Int

    @Query("SELECT * FROM movieList WHERE movieId = :movieId")
    fun getMovieList(movieId: String): List<MovieListDto>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovieList(vararg movieListDto: MovieListDto)

    @Update
    fun updateMovieList(vararg movieListDto: MovieListDto): Int
}