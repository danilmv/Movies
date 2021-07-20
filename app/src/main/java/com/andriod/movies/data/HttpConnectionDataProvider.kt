package com.andriod.movies.data

import android.util.Log
import com.andriod.movies.BuildConfig
import com.andriod.movies.entity.Genre
import com.andriod.movies.entity.Movie
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class HttpConnectionDataProvider : DataProvider() {

    override fun startService() {
        errorMessage = ""

        requestData("https://api.themoviedb.org/3/trending/movie/week",
            data,
            { e: java.lang.Exception -> errorMessage = "init: exception: ${e.message}" },
            { errorMessage = "no data received" }
        )

        requestGenres()
    }

    private fun requestData(
        url: String,
        data: MutableMap<String, Movie>,
        exceptionCallback: (java.lang.Exception) -> Unit = {},
        dataNotFoundCallback: () -> Unit = {},
    ) {
        val connection =
            URL("${url}?&api_key=${BuildConfig.MOVIE_API_KEY}")
                .openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.readTimeout = 10_000

        Thread {
            try {
                BufferedReader(InputStreamReader(connection.inputStream)).use {
                    for (movie in Movie.jsonTrendingToList(it.readLines().joinToString())) {
                        data[movie.id] = movie
                    }
                    updateGenres()
                    Log.d(TAG, "data.size = ${data.size}")
                    notifySubscribers(DataProvider.Companion.SubscriberType.DATA)
                }
            } catch (e: Exception) {
                Log.d(TAG, "init: exception: ${e.message}")
                exceptionCallback.invoke(e)
                return@Thread
            } finally {
                connection.disconnect()
            }

            if (data.isEmpty()) {
                dataNotFoundCallback.invoke()
            }
        }.start()
    }

    override fun findMovies(query: String) {
        searchResultsData.clear()
        startSearch("https://api.themoviedb.org/3/search/multi?query=$query")
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

        Thread {
            try {
                BufferedReader(InputStreamReader(connection.inputStream)).use {
                    for (movie in Movie.jsonTrendingToList(it.readLines().joinToString())) {
                        searchResultsData[movie.id] = movie
                    }
                    updateGenres(searchResultsData)
                    Log.d(TAG, "searchResultsData.size = ${searchResultsData.size}")
                    notifySubscribers(DataProvider.Companion.SubscriberType.SEARCH)
                }
            } catch (e: Exception) {
                Log.d(TAG, "init: exception: ${e.message}")
                exceptionCallback.invoke(e)
                return@Thread
            } finally {
                connection.disconnect()
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

        Thread {
            try {
                BufferedReader(InputStreamReader(connection.inputStream)).use {
                    data[movie.id]?.populateData(Movie.jsonDetailsToObject(it.readLines()
                        .joinToString()))
                    notifySubscribers(DataProvider.Companion.SubscriberType.DATA)
                }
            } catch (e: Exception) {
                Log.d(TAG, "init: exception: ${e.message}")
                return@Thread
            } finally {
                connection.disconnect()
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
            for (i in movie.genre.indices) {
                movie._genre[i] = genres[movie._genre[i].toInt()]?.name ?: "?"
            }
            movie.isGenreUpdated = true
        }
        notifySubscribers(DataProvider.Companion.SubscriberType.DATA)
    }

    companion object {
        const val TAG = "@@HttpConnectionDataPr"
    }
}