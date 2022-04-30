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
    var originalTitle: String = ""

    @SerializedName("runtime")
    var runtime: String = ""

    @SerializedName("release_date")
    var released: String = ""

    @SerializedName("genre_ids")
    var _genre: MutableList<String> = mutableListOf()

    //    @SerializedName("Director") val director: String,
//    @SerializedName("Actors") val actors: String,
    @SerializedName("overview")
    var plot: String = ""

    @SerializedName("poster_path")
    var _poster: String? = null

    val poster: String
        get() = if (_poster == null) "" else "https://image.tmdb.org/t/p/w500/$_poster"

    @SerializedName("vote_average")
    var rating: String = ""

    @SerializedName("vote_count")
    var votes: String = ""

    @SerializedName("revenue")
    var _revenue: Int? = null

    @SerializedName("media_type")
    var _type: String = ""

    @SerializedName("budget")
    var budget: String = ""

    @SerializedName("backdrop_path")
    var _background: String? = null
    val background: String?
        get() = if (_background == null) null else "https://image.tmdb.org/t/p/w500/$_background"

    var isFavorite: Boolean = false
    val year: String
        get() = if (released.isBlank()) "????" else released.substring(0, 4)

    val title: String
        get() = _title ?: name ?: "?"

    val genre: MutableList<String> = mutableListOf()

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

    val videos = mutableMapOf<String, Video>()

    var isSavedToDB = false

    fun addList(list: String) {
        if (!lists.contains(list)) {
            lists.add(list)
            lists.remove("")
            isSavedToDB = false
        }
    }

    fun populateData(movie: Movie) {
        if (id != movie.id) return

        if (movie._title != null) _title = movie._title
        if (movie.name != null) name = movie.name
        if (movie.originalTitle != "") originalTitle = movie.originalTitle
        if (movie.runtime != "") runtime = movie.runtime
        if (movie.released != "") released = movie.released
        if (movie.plot != "") plot = movie.plot
        if (movie._poster != null) _poster = movie.poster
        if (movie.rating != "") rating = movie.rating
        if (movie.votes != "") votes = movie.votes
        if (movie._revenue != null) _revenue = movie._revenue
        if (movie._type != "") _type = movie._type
        if (movie.budget != "") budget = movie._type
        if (movie.isDetailsReceived) isDetailsReceived = movie.isDetailsReceived
        if (movie.isFavorite) isFavorite = movie.isFavorite
        if (movie._background != null) _background = movie._background

        isSavedToDB = false
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
                ""
            }
            movie.runtime = try {
                jsonObj.getString("runtime")
            } catch (exception: JSONException) {
                ""
            }
            movie.released = try {
                jsonObj.getString("release_date")
            } catch (exception: JSONException) {
                ""
            }
            movie._genre = genres
            movie.plot = try {
                jsonObj.getString("overview")
            } catch (exception: JSONException) {
                ""
            }
            movie._poster = try {
                jsonObj.getString("poster_path")
            } catch (exception: JSONException) {
                null
            }
            movie.rating = try {
                jsonObj.getString("vote_average")
            } catch (exception: JSONException) {
                ""
            }
            movie.votes = try {
                jsonObj.getString("vote_count")
            } catch (exception: JSONException) {
                ""
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
                ""
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
                ""
            }
            movie.runtime = try {
                jsonObj.getString("runtime")
            } catch (exception: JSONException) {
                ""
            }
            movie.released = try {
                jsonObj.getString("release_date")
            } catch (exception: JSONException) {
                ""
            }
            movie._genre = genres
            movie.plot = try {
                jsonObj.getString("overview")
            } catch (exception: JSONException) {
                ""
            }
            movie._poster = try {
                jsonObj.getString("poster_path")
            } catch (exception: JSONException) {
                null
            }
            movie.rating = try {
                jsonObj.getString("vote_average")
            } catch (exception: JSONException) {
                ""
            }
            movie.votes = try {
                jsonObj.getString("vote_count")
            } catch (exception: JSONException) {
                ""
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
