package com.andriod.movies.data

import android.util.Log
import com.andriod.movies.BuildConfig
import com.andriod.movies.entity.Genre
import com.andriod.movies.entity.Movie
import com.andriod.movies.statusbar.StatusManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class HttpConnectionDataProvider : DataProvider() {

    private val dataRequestStatusGroup = 1

    override fun startService() {
        errorMessage = ""

        requestData("https://api.themoviedb.org/3/trending/movie/week",
            data,
            "trending",
            { message: String -> errorMessage = message },
            { errorMessage = "no data received" }
        )

        requestData("https://api.themoviedb.org/3/movie/latest", listName = "latest")
        requestData("https://api.themoviedb.org/3/movie/now_playing", listName = "now playing")
        requestData("https://api.themoviedb.org/3/movie/popular", listName = "popular")
        requestData("https://api.themoviedb.org/3/movie/top_rated", listName = "top rated")
        requestData("https://api.themoviedb.org/3/movie/upcoming", listName = "upcoming")

        requestGenres()
    }

    private fun requestData(
        url: String,
        data: MutableMap<String, Movie> = this.data,
        listName: String,
        exceptionCallback: (String) -> Unit = {},
        dataNotFoundCallback: () -> Unit = {},
    ) {
        val connection =
            URL("${url}?api_key=${BuildConfig.MOVIE_API_KEY}")
                .openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.readTimeout = 10_000

        val statusId = StatusManager.create("$listName data requested", dataRequestStatusGroup)
        Thread {

            try {
                BufferedReader(InputStreamReader(connection.inputStream)).use {
                    for (movie in Movie.jsonTrendingToList(it.readLines().joinToString())) {
                        if (data.containsKey(movie.id)) {
                            data[movie.id]?.addList(listName)
                        } else {
                            data[movie.id] = movie.apply {
                                _type = Movie.Companion.TYPE.TYPE_MOVIE.value
                                addList(listName)
                            }
                        }
                    }
                    updateGenres()
                    Log.d(TAG, "requestData for $listName: data.size = ${data.size}")
                    notifySubscribers(DataProvider.Companion.SubscriberType.DATA)
                    StatusManager.change(statusId, "$listName data received")
                }
            } catch (e: Exception) {
                val message = "requestData for $listName: exception: ${e.message}"
                Log.d(TAG, message)
                exceptionCallback.invoke(message)
                StatusManager.change(statusId, "$listName error occurred")
                return@Thread
            } finally {
                connection.disconnect()
                StatusManager.close(statusId)
            }

            if (data.isEmpty()) {
                dataNotFoundCallback.invoke()
            }
        }.also { it.isDaemon = true }
            .start()
    }

    override fun findMovies(query: String) {
        searchResultsData.clear()
        startSearch("https://api.themoviedb.org/3/search/movie?query=$query")
    }

    private fun startSearch(
        url: String,
        exceptionCallback: (java.lang.Exception) -> Unit = {},
        dataNotFoundCallback: () -> Unit = {},
    ) {
        val connection =
            URL("${url}?&api_key=${BuildConfig.MOVIE_API_KEY}")
                .openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.readTimeout = 10_000

        val statusId = StatusManager.create(message = "searching started")

        Thread {
            try {
                BufferedReader(InputStreamReader(connection.inputStream)).use {
                    for (movie in Movie.jsonTrendingToList(it.readLines().joinToString())) {
                        movie._type = Movie.Companion.TYPE.TYPE_MOVIE.value
                        searchResultsData[movie.id] = data[movie.id] ?: movie
                    }
                    updateGenres(searchResultsData)

                    Log.d(TAG, "startSearch: searchResultsData.size = ${searchResultsData.size}")
                    notifySubscribers(DataProvider.Companion.SubscriberType.SEARCH)
                    StatusManager.change(statusId, "searching finished")
                }
            } catch (e: Exception) {
                Log.d(TAG, "startSearch: exception: ${e.message}")
                exceptionCallback.invoke(e)
                return@Thread
            } finally {
                connection.disconnect()
                StatusManager.close(statusId)
            }

            if (searchResultsData.isEmpty()) {
                dataNotFoundCallback.invoke()
            }
        }.start()
    }

    override fun getMovieDetails(movie: Movie) {
        super.getMovieDetails(movie)

        requestMovieDetails(movie)
        Log.d(TAG, "getMovieDetails() called with: movie = $movie")
    }

    private fun requestMovieDetails(
        movie: Movie,
    ) {
        val connection =
            when (movie.type) {
                Movie.Companion.TYPE.TYPE_MOVIE -> URL("https://api.themoviedb.org/3/movie/${movie.id}?&api_key=${BuildConfig.MOVIE_API_KEY}")
                    .openConnection() as HttpsURLConnection
                Movie.Companion.TYPE.TYPE_TV_SERIES -> URL("https://api.themoviedb.org/3/tv/${movie.id}?&api_key=${BuildConfig.MOVIE_API_KEY}")
                    .openConnection() as HttpsURLConnection
                null -> return
            }
        connection.requestMethod = "GET"
        connection.readTimeout = 10_000

        val statusId = StatusManager.create(message = "details for ${movie.title} requested")

        Thread {
            try {
                BufferedReader(InputStreamReader(connection.inputStream)).use {

                    if (data.containsKey(movie.id)) {
                        data[movie.id]?.populateData(Movie.jsonDetailsToObject(it.readLines()
                            .joinToString()))
                    } else {
                        data[movie.id] = Movie.jsonDetailsToObject(it.readLines().joinToString())
                    }
                    updateGenres(data)
                    notifySubscribers(DataProvider.Companion.SubscriberType.DATA)

                    StatusManager.change(statusId, "details received")
                }
            } catch (e: Exception) {
                Log.d(TAG, "init: exception: ${e.message}")
                return@Thread
            } finally {
                connection.disconnect()
                StatusManager.close(statusId)
            }
        }.start()
    }

    private fun requestGenres() {
        if (isGenresLoaded) return

        val connection =
            URL("https://api.themoviedb.org/3/genre/movie/list?api_key=${BuildConfig.MOVIE_API_KEY}")
                .openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.readTimeout = 10_000

        Thread {
            try {
                BufferedReader(InputStreamReader(connection.inputStream)).use {
                    for (genre in Genre.jsonToList(it.readLines().joinToString())) {
                        genres[genre.id] = genre
                    }
                    Log.d(TAG, "genres.size = ${genres.size}")
                }
                isGenresLoaded = true
                updateGenres()
            } catch (e: Exception) {
                Log.d(TAG, "requestGenres: exception: ${e.message}")
            } finally {
                connection.disconnect()
            }

        }.start()
    }

    private fun updateGenres(data: MutableMap<String, Movie> = this.data) {
        if (!isGenresLoaded) return

        for (movie in data.values.filter { !it.isGenreUpdated }) {
            movie.isGenreUpdated = true
            if (movie._genre.isNullOrEmpty()) {
                continue
            }
            for (i in movie.genre.indices) {
                movie._genre[i] = genres[movie._genre[i].toInt()]?.name ?: "?"
            }
        }
        notifySubscribers(DataProvider.Companion.SubscriberType.DATA)
    }

    companion object {
        const val TAG = "@@HttpConnectionDataPr"
    }
}