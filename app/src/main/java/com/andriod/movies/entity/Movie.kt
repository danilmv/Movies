package com.andriod.movies.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.json.JSONException
import org.json.JSONObject

@Parcelize
data class Movie(
    @SerializedName("id") val id: String,
    @SerializedName("title") var _title: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("original_title") val originalTitle: String,
    @SerializedName("runtime") val runtime: String?,
    @SerializedName("release_date") val released: String?,
    @SerializedName("genre_ids") var _genre: List<String>?,
//    @SerializedName("Director") val director: String,
//    @SerializedName("Actors") val actors: String,
    @SerializedName("overview") val plot: String,
    @SerializedName("poster_path") val poster: String,
    @SerializedName("vote_average") val rating: String,
    @SerializedName("vote_count") val votes: String,
    @SerializedName("revenue") val revenue: String?,
    @SerializedName("media_type") var type: String = "",
    var isFavorite: Boolean = false,

    ) : Parcelable {
    val year: String
        get() = released?.substring(0, 4) ?: "????"

    val title: String
        get() = _title ?: name ?: "?"

    val genre: List<String>
        get() = if (_genre?.isNotEmpty() == true) _genre!! else listOf("?")

    companion object {
        const val TYPE_MOVIE = "movie"
        const val TYPE_TV_SERIES = "tv"

        fun jsonTrendingToList(raw: String): List<Movie> {
            val result = mutableListOf<Movie>()
            val jsonTrendingObj = JSONObject(raw)
            val results = jsonTrendingObj.getJSONArray("results")
            for (i in 0 until results.length()) {
                result.add(jsonToObject(results.getString(i)))
            }

            return result
        }

        fun jsonToObject(raw: String): Movie {
            val jsonObj = JSONObject(raw)

            val genres = mutableListOf<String>()
            val jsonGenres = jsonObj.getJSONArray("genre_ids")
            for (i in 0 until jsonGenres.length()) {
                genres.add(jsonGenres.getString(i))
            }

            return Movie(
                jsonObj.getString("id"),
                jsonObj.getString("title"),
                try {
                    jsonObj.getString("name")
                } catch (exception: JSONException) {
                    null
                },
                jsonObj.getString("original_title"),
                try {
                    jsonObj.getString("runtime")
                } catch (exception: JSONException) {
                    null
                },
                jsonObj.getString("release_date"),
                genres,
                jsonObj.getString("overview"),
                jsonObj.getString("poster_path"),
                jsonObj.getString("vote_average"),
                jsonObj.getString("vote_count"),
                try {
                    jsonObj.getString("revenue")
                } catch (exception: JSONException) {
                    null
                },
                jsonObj.getString("media_type"),
            )
        }
    }
}
