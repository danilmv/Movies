package com.andriod.movies.data

import com.andriod.movies.BuildConfig
import com.andriod.movies.entity.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDBService {
    @GET("3/movie/{list}?api_key=${BuildConfig.MOVIE_API_KEY}")
    fun getMovieList(
        @Path("list") listName: String,
        @Query("page") page: Int,
    ): Call<MovieList>

    @GET("3/genre/movie/list?api_key=${BuildConfig.MOVIE_API_KEY}")
    fun getGenres(): Call<Genres>

    @GET("3/trending/movie/week?api_key=${BuildConfig.MOVIE_API_KEY}")
    fun getTrending(
        @Query("page") page: Int,
    ): Call<Trending>

    @GET("3/{type}/{id}?api_key=${BuildConfig.MOVIE_API_KEY}")
    fun getDetails(
        @Path("type") type: String,
        @Path("id") id: String,
    ): Call<Movie>

    @GET("3/search/movie?api_key=${BuildConfig.MOVIE_API_KEY}")
    fun getSearching(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
    ): Call<SearchResults>

    @GET("3/movie/{id}/videos?api_key=${BuildConfig.MOVIE_API_KEY}")
    fun getVideos(
        @Path("id") id: String
    ): Call<MovieVideos>
}