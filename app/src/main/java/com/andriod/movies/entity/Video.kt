package com.andriod.movies.entity

data class Video(
    val name: String,
    val key: String,
    val site: String,
    val type: String,
) {
    val link: String
        get() = "https://www.youtube.com/watch?v=$key"
}
