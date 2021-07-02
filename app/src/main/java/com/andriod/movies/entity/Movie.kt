package com.andriod.movies.entity

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("imdbID") val id: String,
    @SerializedName("Title") val title: String,
    @SerializedName("Year") val year: Int,
    @SerializedName("Rated") val rated: String,
    @SerializedName("Runtime") val runtime: String,
    @SerializedName("Genre") val genre: String,
    @SerializedName("Director") val director: String,
    @SerializedName("Actors") val actors: String,
    @SerializedName("Plot") val plot: String,
    @SerializedName("Poster") val poster: String,
    val imdbRating: String,
    val imdbVotes: String,
    @SerializedName("BoxOffice") val boxOffice: String,
    @SerializedName("Type") val type: String,
)
