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
    @SerializedName("name") var name: String?,
    @SerializedName("original_title") var originalTitle: String?,
    @SerializedName("runtime") var runtime: String?,
    @SerializedName("release_date") var released: String?,
    @SerializedName("genre_ids") var _genre: List<String>?,
//    @SerializedName("Director") val director: String,
//    @SerializedName("Actors") val actors: String,
    @SerializedName("overview") var plot: String?,
    @SerializedName("poster_path") var poster: String?,
    @SerializedName("vote_average") var rating: String?,
    @SerializedName("vote_count") var votes: String?,
    @SerializedName("revenue") var _revenue: Int?,
    @SerializedName("media_type") var _type: String? = "",
    @SerializedName("budget") var budget: String? = "",
    var isFavorite: Boolean = false,

    ) : Parcelable {
    val year: String
        get() = released?.substring(0, 4) ?: "????"

    val title: String
        get() = _title ?: name ?: "?"

    val genre: List<String>
        get() = if (_genre?.isNotEmpty() == true) _genre!! else listOf("?")

    var isDetailsReceived = false

    val type: TYPE?
        get() {
            return when(_type){
                "movie"->TYPE.TYPE_MOVIE
                "tv"->TYPE.TYPE_TV_SERIES
                else -> null
            }
        }

    val revenue:String get(){
        val rev = _revenue
        return if (rev != null && rev > 0) rev.toString() else "?"
    }

    fun populateData(movie: Movie){
        if (id != movie.id)return

        if (movie._title != null) _title = movie._title
        if (movie.name != null) name = movie.name
        if (movie.originalTitle != null) originalTitle = movie.originalTitle
        if (movie.runtime != null) runtime = movie.runtime
        if (movie.released != null) released = movie.released
        if (movie.plot != null) plot = movie.plot
        if (movie.poster != null) poster = movie.poster
        if (movie.rating != null) rating = movie.rating
        if (movie.votes != null) votes = movie.votes
        if (movie._revenue != null) _revenue = movie._revenue
        if (movie._type != null) _type = movie._type
        if (movie.budget != null) budget = movie._type
        if (movie.isDetailsReceived) isDetailsReceived = movie.isDetailsReceived
    }

    companion object {
        enum class TYPE(val value: String) {
            TYPE_MOVIE("movie"),
            TYPE_TV_SERIES("tv"),
        }

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
            try {
                val jsonGenres = jsonObj.getJSONArray("genre_ids")
                for (i in 0 until jsonGenres.length()) {
                    genres.add(jsonGenres.getString(i))
                }
            } catch (exception: JSONException) {
            }

            return Movie(
                jsonObj.getString("id"),
                try {
                    jsonObj.getString("title")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getString("name")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getString("original_title")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getString("runtime")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getString("release_date")
                } catch (exception: JSONException) {
                    null
                },
                genres,
                try {
                    jsonObj.getString("overview")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getString("poster_path")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getString("vote_average")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getString("vote_count")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getInt("revenue")
                } catch (exception: JSONException) {
                    0
                },
                try {
                    jsonObj.getString("media_type")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getString("budget")
                } catch (exception: JSONException) {
                    null
                },
            )
        }

        fun jsonDetailsToObject(raw: String): Movie {
            val jsonObj = JSONObject(raw)

            val genres = mutableListOf<String>()
            try {
                val jsonGenres = jsonObj.getJSONArray("genres")
                for (i in 0 until jsonGenres.length()) {
                    genres.add(jsonGenres.getJSONObject(i).getString("id"))
                }
            } catch (exception: JSONException) {
            }

            return Movie(
                jsonObj.getString("id"),
                try {
                    jsonObj.getString("title")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getString("name")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getString("original_title")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getString("runtime")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getString("release_date")
                } catch (exception: JSONException) {
                    null
                },
                genres,
                try {
                    jsonObj.getString("overview")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getString("poster_path")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getString("vote_average")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getString("vote_count")
                } catch (exception: JSONException) {
                    null
                },
                try {
                    jsonObj.getInt("revenue")
                } catch (exception: JSONException) {
                    0
                },
                try {
                    jsonObj.getString("media_type")
                } catch (exception: JSONException) {
                    null
                }
            ).also { it.isDetailsReceived = true }
        }
    }
}
