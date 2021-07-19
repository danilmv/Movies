package com.andriod.movies.data

import com.andriod.movies.BuildConfig
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class HttpConnectionDataProvider : DataProvider() {

    init {
        val connection =
            URL("https://api.themoviedb.org/3/trending/movie/week?api_key=${BuildConfig.MOVIE_API_KEY}")
                .openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.readTimeout = 10_000

        Thread {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readLines().joinToString()
        }.start()
    }

    override fun findMovies(query: String) {
        TODO("Not yet implemented")
    }
}