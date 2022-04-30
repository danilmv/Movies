package com.andriod.movies.entity

data class Trending(
    val page: Int,
    val results: List<Movie>,
    val total_pages: Int,
    val total_results: Int
)
