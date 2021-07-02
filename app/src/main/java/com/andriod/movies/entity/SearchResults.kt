package com.andriod.movies.entity

import com.google.gson.annotations.SerializedName

data class SearchResults(
    @SerializedName("Search") val search: List<Movie>,
)
