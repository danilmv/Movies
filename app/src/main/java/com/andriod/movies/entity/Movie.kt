package com.andriod.movies.entity

data class Movie(
    val id: String,
    val title: String,
    val year: Int,
    val rated: String,
    val runtime: String,
    val genre: String,
    val director: String,
    val actors: String,
    val plot: String,
    val posters: String,
    val imdbRating: String,
    val imdbVotes: String,
    val boxOffice: String,
    val type: String
)
