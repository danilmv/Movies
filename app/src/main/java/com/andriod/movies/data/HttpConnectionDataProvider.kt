package com.andriod.movies.data

import android.util.Log
import android.widget.Toast
import com.andriod.movies.BuildConfig
import com.andriod.movies.entity.Movie
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class HttpConnectionDataProvider : DataProvider() {

    override fun startService() {
        errorMessage = ""

        val connection =
            URL("https://api.themoviedb.org/3/trending/movie/week?api_key=${BuildConfig.MOVIE_API_KEY}")
                .openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.readTimeout = 10_000

        Thread {
            try {
                BufferedReader(InputStreamReader(connection.inputStream)).use {
                    for (movie in Movie.jsonTrendingToList(it.readLines().joinToString())) {
                        data[movie.id] = movie
                    }
                    Log.d(TAG, "data.size = ${data.size}")
                    notifySubscribers(DataProvider.Companion.SubscriberType.DATA)
                }
            } catch (e: Exception) {
                Log.d(TAG, "init: exception: ${e.message}")
                errorMessage = "init: exception: ${e.message}"
                return@Thread
            } finally {
                connection.disconnect()
            }

            if (data.isEmpty()) {
                errorMessage = "no data received"
            }
        }.start()
    }

    override fun findMovies(query: String) {
        TODO("Not yet implemented")
    }

    companion object {
        const val TAG = "@@HttpConnectionDataPr"
    }
}