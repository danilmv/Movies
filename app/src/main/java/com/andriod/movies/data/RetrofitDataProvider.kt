package com.andriod.movies.data

import com.andriod.movies.entity.Genres
import com.andriod.movies.entity.Movie
import com.andriod.movies.entity.MovieList
import com.andriod.movies.entity.Trending
import com.andriod.movies.statusbar.StatusManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitDataProvider() : DataProvider() {
    private val dataRequestStatusGroup = 1
    private var retrofit: Retrofit? = null
    private lateinit var service: TheMovieDBService


    override fun startService() {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.themoviedb.org/")
                .build()

            service = retrofit!!.create(TheMovieDBService::class.java)
        }

        errorMessage = ""

        requestTrending("trending", { message: String -> errorMessage = message })

        requestMovieList("latest", "latest")
        requestMovieList("now_playing", "now playing")
        requestMovieList("popular", "popular")
        requestMovieList("top_rated", "top rated")
        requestMovieList("upcoming", "upcoming")

        requestGenres()
    }

    private fun requestMovieList(
        url: String,
        listName: String,
        exceptionCallback: (String) -> Unit = {},
        data: MutableMap<String, Movie> = this.data,
        page: Int = 1,
    ) {
        val statusId = StatusManager.create("waiting for: $listName data requested, page = $page",
            dataRequestStatusGroup)
        service.getMovieList(url, page).enqueue(
            object : Callback<MovieList> {
                override fun onResponse(call: Call<MovieList>, response: Response<MovieList>) {
                    if (response.isSuccessful) {
                        response.body()?.results?.forEach { movie ->
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
                        notifySubscribers(Companion.SubscriberType.DATA)
                        StatusManager.change(statusId, "$listName data received")
                    } else {
                        exceptionCallback.invoke("Failed to load data for $listName")
                    }

                    StatusManager.close(statusId)
                }

                override fun onFailure(call: Call<MovieList>, t: Throwable) {
                    StatusManager.change(statusId,
                        "Failed to load data for $listName: ${t.message}")
                    t.message?.let { exceptionCallback.invoke(it) }

                    StatusManager.close(statusId)
                }

            }
        )
    }

    private fun requestGenres() {
        if (isGenresLoaded) return


        val statusId = StatusManager.create("waiting for: genres data requested")
        service.getGenres().enqueue(
            object : Callback<Genres> {
                override fun onResponse(call: Call<Genres>, response: Response<Genres>) {
                    for (genre in response.body()?.genres!!) {
                        genres[genre.id] = genre
                    }
                    if (genres.isNotEmpty()) {
                        isGenresLoaded = true
                        updateGenres()
                    }
                    StatusManager.close(statusId)
                }

                override fun onFailure(call: Call<Genres>, t: Throwable) {
                    StatusManager.close(statusId)
                }
            })
    }

    private fun requestTrending(
        listName: String,
        exceptionCallback: (String) -> Unit = {},
        data: MutableMap<String, Movie> = this.data,
        page: Int = 1,
    ) {
        val statusId = StatusManager.create("waiting for: $listName data requested, page = $page",
            dataRequestStatusGroup)
        service.getTrending(page).enqueue(
            object : Callback<Trending> {
                override fun onResponse(call: Call<Trending>, response: Response<Trending>) {
                    if (response.isSuccessful) {
                        response.body()?.results?.forEach { movie ->
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
                        notifySubscribers(Companion.SubscriberType.DATA)
                        StatusManager.change(statusId, "$listName data received")
                    } else {
                        exceptionCallback.invoke("Failed to load data for $listName")
                    }

                    StatusManager.close(statusId)
                }

                override fun onFailure(call: Call<Trending>, t: Throwable) {
                    StatusManager.change(statusId,
                        "Failed to load data for $listName: ${t.message}")
                    t.message?.let { exceptionCallback.invoke(it) }

                    StatusManager.close(statusId)
                }

            }
        )
    }

    override fun findMovies(query: String) {
        TODO("Not yet implemented")
    }
}