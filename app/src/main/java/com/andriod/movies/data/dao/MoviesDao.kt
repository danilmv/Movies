package com.andriod.movies.data.dao

import androidx.room.*
import com.andriod.movies.entity.room.*

@Dao
interface MoviesDao {
//    Movie
    @Query("SELECT * FROM movies")
    fun getAllMovies(): List<MovieDto>

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovie(id: String): MovieDto

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovie(movieDto: MovieDto): Long

    @Update
    fun updateMovie(movieDto: MovieDto): Int

    @Delete
    fun deleteMovie(movieDto: MovieDto)

//    MovieList

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

//    MovieGenre

    @Query("SELECT * FROM Genres WHERE lang = :lang")
    fun getGenres(lang: String = "EN"): List<GenreDto>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertGenres(vararg genreDto: GenreDto)

    @Update
    fun updateGenres(vararg genreDto: GenreDto): Int

    @Query("SELECT genreId FROM MovieGenre WHERE movieId = :movieId")
    fun getMovieGenres(movieId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovieGenres(vararg movieGenreDto: MovieGenreDto)

    @Update
    fun updateMovieGenres(vararg movieGenreDto: MovieGenreDto): Int
}