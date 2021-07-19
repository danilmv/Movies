package com.andriod.movies.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    @SerializedName("id") val id: String,
    @SerializedName("title") var _title: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("original_title") val originalTitle: String,
    @SerializedName("runtime") val runtime: String,
    @SerializedName("release_date") val released: String?,
    @SerializedName("genre_ids") var _genre: List<String>?,
//    @SerializedName("Director") val director: String,
//    @SerializedName("Actors") val actors: String,
    @SerializedName("overview") val plot: String,
    @SerializedName("poster_path") val poster: String,
    @SerializedName("vote_average") val rating: String,
    @SerializedName("vote_count") val votes: String,
    @SerializedName("revenue") val revenue: String,
    @SerializedName("media_type") var type: String = "",
    var isFavorite: Boolean = false,

    ) : Parcelable {
    val year: String
        get() = released?.substring(0, 4) ?: "????"

    val title: String
        get() = _title ?: name ?: "?"

    val genre: List<String>
        get() = if (_genre?.isNotEmpty() ?: false) _genre!! else listOf("?")
}
