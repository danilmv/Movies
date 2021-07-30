package com.andriod.movies.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.json.JSONException
import org.json.JSONObject

@Parcelize
class Movie : Parcelable {
    @SerializedName("id")
    lateinit var id: String

    @SerializedName("title")
    var _title: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("original_title")
    var originalTitle: String? = null

    @SerializedName("runtime")
    var runtime: String? = null

    @SerializedName("release_date")
    var released: String? = null

    @SerializedName("genre_ids")
    var _genre: MutableList<String> = mutableListOf<String>()

    //    @SerializedName("Director") val director: String,
//    @SerializedName("Actors") val actors: String,
    @SerializedName("overview")
    var plot: String? = null

    @SerializedName("poster_path")
    var poster: String? = null

    @SerializedName("vote_average")
    var rating: String? = null

    @SerializedName("vote_count")
    var votes: String? = null

    @SerializedName("revenue")
    var _revenue: Int? = null

    @SerializedName("media_type")
    var _type: String = ""

    @SerializedName("budget")
    var budget: String? = ""
    var isFavorite: Boolean = false
    val year: String
        get() = if (released.isNullOrBlank()) "????" else released!!.substring(0, 4)

    val title: String
        get() = _title ?: name ?: "?"

    val genre: List<String>
        get() = if (_genre.isNotEmpty()) _genre else listOf("?")

    var isDetailsReceived = false

    var lists: MutableList<String> = mutableListOf("")

    val type: TYPE?
        get() {
            return when (_type) {
                "movie" -> TYPE.TYPE_MOVIE
                "tv" -> TYPE.TYPE_TV_SERIES
                else -> null
            }
        }

    val revenue: String
        get() {
            val rev = _revenue
            return if (rev != null && rev > 0) rev.toString() else "?"
        }

    var isGenreUpdated = false

    fun addList(list: String) {
        if (!lists.contains(list)) lists.add(list)
        lists.remove("")
    }

    fun populateData(movie: Movie) {
        if (id != movie.id) return

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
        if (movie._type != "") _type = movie._type
        if (movie.budget != null) budget = movie._type
        if (movie.isDetailsReceived) isDetailsReceived = movie.isDetailsReceived
        if (movie.isFavorite) isFavorite = movie.isFavorite
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

            val movie = Movie()
            movie.id = jsonObj.getString("id")
            movie._title = try {
                jsonObj.getString("title")
            } catch (exception: JSONException) {
                null
            }
            movie.name = try {
                jsonObj.getString("name")
            } catch (exception: JSONException) {
                null
            }
            movie.originalTitle = try {
                jsonObj.getString("original_title")
            } catch (exception: JSONException) {
                null
            }
            movie.runtime = try {
                jsonObj.getString("runtime")
            } catch (exception: JSONException) {
                null
            }
            movie.released = try {
                jsonObj.getString("release_date")
            } catch (exception: JSONException) {
                null
            }
            movie._genre = genres
            movie.plot = try {
                jsonObj.getString("overview")
            } catch (exception: JSONException) {
                null
            }
            movie.poster = try {
                jsonObj.getString("poster_path")
            } catch (exception: JSONException) {
                null
            }
            movie.rating = try {
                jsonObj.getString("vote_average")
            } catch (exception: JSONException) {
                null
            }
            movie.votes = try {
                jsonObj.getString("vote_count")
            } catch (exception: JSONException) {
                null
            }
            movie._revenue = try {
                jsonObj.getInt("revenue")
            } catch (exception: JSONException) {
                0
            }
            movie._type = try {
                jsonObj.getString("media_type")
            } catch (exception: JSONException) {
                ""
            }
            movie.budget = try {
                jsonObj.getString("budget")
            } catch (exception: JSONException) {
                null
            }

            return movie
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

            val movie = Movie()
            movie.id = jsonObj.getString("id")
            movie._title = try {
                jsonObj.getString("title")
            } catch (exception: JSONException) {
                null
            }
            movie.name = try {
                jsonObj.getString("name")
            } catch (exception: JSONException) {
                null
            }
            movie.originalTitle = try {
                jsonObj.getString("original_title")
            } catch (exception: JSONException) {
                null
            }
            movie.runtime = try {
                jsonObj.getString("runtime")
            } catch (exception: JSONException) {
                null
            }
            movie.released = try {
                jsonObj.getString("release_date")
            } catch (exception: JSONException) {
                null
            }
            movie._genre = genres
            movie.plot = try {
                jsonObj.getString("overview")
            } catch (exception: JSONException) {
                null
            }
            movie.poster = try {
                jsonObj.getString("poster_path")
            } catch (exception: JSONException) {
                null
            }
            movie.rating = try {
                jsonObj.getString("vote_average")
            } catch (exception: JSONException) {
                null
            }
            movie.votes = try {
                jsonObj.getString("vote_count")
            } catch (exception: JSONException) {
                null
            }
            movie._revenue = try {
                jsonObj.getInt("revenue")
            } catch (exception: JSONException) {
                0
            }
            movie._type = try {
                jsonObj.getString("media_type")
            } catch (exception: JSONException) {
                ""
            }
            movie.isDetailsReceived = true
            return movie
        }
    }
}
