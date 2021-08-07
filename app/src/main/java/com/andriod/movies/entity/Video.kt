package com.andriod.movies.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    val name: String,
    val key: String,
    val site: String,
    val type: String,
) : Parcelable {
    val link: String
        get() = "https://www.youtube.com/watch?v=$key"
}
