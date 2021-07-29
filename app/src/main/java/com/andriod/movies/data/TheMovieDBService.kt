package com.andriod.movies.data

import com.andriod.movies.BuildConfig
import com.andriod.movies.entity.Movie
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TheMovieDBService {
    @GET("/3/movie/{list}?&api_key=${BuildConfig.MOVIE_API_KEY}&page={page}")
    fun getMovie(@Path("list") list: String, @Path("page") page: Int = 1): Call<List<Movie>>
}